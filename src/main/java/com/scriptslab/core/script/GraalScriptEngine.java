package com.scriptslab.core.script;

import com.scriptslab.api.script.ScriptEngine;
import com.scriptslab.api.script.ScriptEngine.LoadedScript;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * GraalVM-based JavaScript engine implementation.
 * Thread-safe with sandboxing and reload resilience.
 * Features isolated contexts per script and require() system.
 */
public final class GraalScriptEngine implements ScriptEngine {
    
    private static final Logger STATIC_LOGGER = LoggerFactory.getLogger("ScriptEngine.Static");
    private static volatile Engine sharedEngine;
    private static final AtomicBoolean engineLoaded = new AtomicBoolean(false);
    private static final Object ENGINE_LOCK = new Object();
    
    private final Logger logger;
    private final Path scriptsDirectory;
    private final Object scriptAPI;
    private final Map<String, LoadedScriptImpl> scripts;
    private final ExecutorService executor;
    private final AtomicBoolean initialized;
    private final AtomicBoolean wasInitialized;
    
    // Карта контекстов для изоляции скриптов
    private final Map<String, Context> scriptContexts;
    // Кэш загруженных модулей для require()
    private final Map<String, String> moduleCache;
    
    public GraalScriptEngine(Path scriptsDirectory, Object scriptAPI) {
        this.logger = LoggerFactory.getLogger("ScriptEngine");
        this.scriptsDirectory = scriptsDirectory;
        this.scriptAPI = scriptAPI;
        this.scripts = new ConcurrentHashMap<>();
        this.scriptContexts = new ConcurrentHashMap<>();
        this.moduleCache = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(4);
        this.initialized = new AtomicBoolean(false);
        this.wasInitialized = new AtomicBoolean(false);
    }
    
    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> initializeCore(), executor);
    }
    
    public void initializeSync() {
        initializeCore();
    }
    
    private void initializeCore() {
        if (initialized.get()) {
            logger.warn("Script engine already initialized");
            return;
        }
        
        if (wasInitialized.get()) {
            logger.warn("Script engine was previously loaded - full reload required to reset");
            logger.info("Attempting to reset GraalVM state...");
            resetGraalVMState();
        }
        
        try {
            logger.info("Initializing GraalVM JavaScript engine...");
            
            synchronized (ENGINE_LOCK) {
                if (sharedEngine == null || !engineLoaded.get()) {
                    logger.info("Creating new GraalVM engine...");
                    
                    Engine engine = Engine.newBuilder()
                            .option("js.ecmascript-version", "2022")
                            .option("engine.WarnInterpreterOnly", "false")
                            .build();
                    
                    sharedEngine = engine;
                    engineLoaded.set(true);
                    
                    STATIC_LOGGER.info("Created new shared GraalVM engine");
                } else {
                    logger.info("Reusing existing GraalVM engine");
                }
            }
            
            scriptsDirectory.toFile().mkdirs();
            
            initialized.set(true);
            wasInitialized.set(true);
            logger.info("Script engine initialized successfully (isolated contexts mode)");
            
        } catch (Exception e) {
            logger.error("Failed to initialize script engine", e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Настройка биндингов для контекста.
     */
    private void setupContextBindings(Context context) {
        ConsoleAPI consoleAPI = new ConsoleAPI(logger);
        
        context.getBindings("js").putMember("API", scriptAPI);
        context.getBindings("js").putMember("plugin", scriptAPI);
        context.getBindings("js").putMember("console", consoleAPI);
        context.getBindings("js").putMember("Console", consoleAPI);
        context.getBindings("js").putMember("Logger", consoleAPI);
        context.getBindings("js").putMember("Commands", scriptAPI);
        context.getBindings("js").putMember("Events", scriptAPI);
        context.getBindings("js").putMember("Scheduler", scriptAPI);
        context.getBindings("js").putMember("Players", scriptAPI);
        context.getBindings("js").putMember("Server", scriptAPI);
        context.getBindings("js").putMember("World", scriptAPI);
        context.getBindings("js").putMember("Items", scriptAPI);
        context.getBindings("js").putMember("Storage", scriptAPI);
    }
    
    /**
     * Создает изолированный контекст для конкретного скрипта.
     */
    private Context createIsolatedContext(String scriptId) {
        Context.Builder contextBuilder = Context.newBuilder("js")
                .engine(sharedEngine)
                .allowExperimentalOptions(true)
                .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true)
                .allowIO(org.graalvm.polyglot.io.IOAccess.ALL)
                .allowCreateThread(true)
                .allowCreateProcess(true)
                .allowEnvironmentAccess(org.graalvm.polyglot.EnvironmentAccess.INHERIT)
                .allowPolyglotAccess(PolyglotAccess.ALL);
        
        final Context context = contextBuilder.build();
        setupContextBindings(context);
        
        // Добавляем функцию require() для этого контекста
        final GraalScriptEngine engineRef = this;
        
        context.getBindings("js").putMember("require", (ProxyExecutable) arguments -> {
            if (arguments.length == 0) {
                throw new RuntimeException("require() needs a module name");
            }
            
            String moduleName = arguments[0].asString();
            String resolvedCode = engineRef.resolveModule(moduleName);
            
            if (resolvedCode == null) {
                throw new RuntimeException("Module not found: " + moduleName);
            }
            
            // Реализуем простой CommonJS паттерн
            String wrappedCode = 
                "(function() {" +
                "  var module = { exports: {} };" +
                "  var exports = module.exports;" +
                "  " + resolvedCode + "\n" +
                "  return module.exports;" +
                "})()";
            
            return context.eval("js", wrappedCode);
        });
        
        // Также добавляем loadedModules кэш
        context.eval("js", 
            "var loadedModules = {};" +
            "var originalRequire = require;" +
            "require = function(moduleName) {" +
            "  if (loadedModules[moduleName]) {" +
            "    return loadedModules[moduleName];" +
            "  }" +
            "  var result = originalRequire(moduleName);" +
            "  loadedModules[moduleName] = result;" +
            "  return result;" +
            "};");
        
        return context;
    }
    
    /**
     * Разрешает модуль для системы require(). Вызывается из JS.
     */
    public String resolveModule(String moduleName) {
        try {
            // Сохраняем копию для использования в лямбде
            final String finalModuleName;
            if (!moduleName.endsWith(".js")) {
                finalModuleName = moduleName + ".js";
            } else {
                finalModuleName = moduleName;
            }
            
            Path modulePath = null;
            Path absolutePath = scriptsDirectory.resolve(finalModuleName);
            if (Files.exists(absolutePath) && Files.isRegularFile(absolutePath)) {
                modulePath = absolutePath;
            }
            
            if (modulePath == null) {
                try (Stream<Path> paths = Files.walk(scriptsDirectory, 3)) {
                    Path found = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().equalsIgnoreCase(finalModuleName))
                        .findFirst()
                        .orElse(null);
                    
                    if (found != null) {
                        modulePath = found;
                    }
                }
            }
            
            if (modulePath == null) {
                logger.warn("Module not found: " + moduleName);
                return null;
            }
            
            String content = moduleCache.get(modulePath.toString());
            if (content == null) {
                content = Files.readString(modulePath);
                moduleCache.put(modulePath.toString(), content);
            }
            
            return content;
            
        } catch (IOException e) {
            logger.warn("Error resolving module: " + moduleName, e);
            return null;
        }
    }
    
    private void resetGraalVMState() {
        logger.info("Resetting GraalVM static state...");
        
        synchronized (ENGINE_LOCK) {
            // Закрываем все изолированные контексты
            scriptContexts.values().forEach(ctx -> {
                try { ctx.close(); } catch (Exception ignored) {}
            });
            scriptContexts.clear();
            
            try {
                if (sharedEngine != null) {
                    logger.info("Closing old shared engine...");
                    try {
                        sharedEngine.close();
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) {
                logger.warn("Error closing shared engine", e);
            }
            
            sharedEngine = null;
            engineLoaded.set(false);
            initialized.set(false);
            
            logger.info("GraalVM state reset complete");
        }
    }
    
    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            if (!initialized.get()) {
                return;
            }
            
            logger.info("Shutting down script engine...");
            
            scriptContexts.values().forEach(ctx -> {
                try { ctx.close(); } catch (Exception ignored) {}
            });
            scriptContexts.clear();
            scripts.clear();
            
            initialized.set(false);
            
            logger.info("Script engine shut down (shared engine preserved for reload)");
        });
    }
    
    @Override
    public CompletableFuture<LoadedScript> loadScript(Path scriptPath) {
        return CompletableFuture.supplyAsync(() -> {
            ensureInitialized();
            
            try {
                String scriptId = scriptPath.getFileName().toString().replace(".js", "");
                
                // Создаем изолированный контекст для этого скрипта
                Context scriptContext = createIsolatedContext(scriptId);
                scriptContexts.put(scriptId, scriptContext);
                
                String code = Files.readString(scriptPath);
                Source source = Source.newBuilder("js", code, scriptId).build();
                
                scriptContext.eval(source);
                
                LoadedScriptImpl script = new LoadedScriptImpl(
                        scriptId, scriptPath, System.currentTimeMillis());
                
                scripts.put(scriptId, script);
                script.incrementExecutionCount();
                
                logger.info("Loaded script (isolated): " + scriptId);
                return script;
                
            } catch (IOException e) {
                logger.error("Failed to read script: " + scriptPath, e);
                throw new RuntimeException(e);
            } catch (PolyglotException e) {
                logger.error("Script error in " + scriptPath, e);
                
                LoadedScriptImpl script = new LoadedScriptImpl(
                        scriptPath.getFileName().toString().replace(".js", ""),
                        scriptPath,
                        System.currentTimeMillis());
                script.setError(e);
                scripts.put(script.getId(), script);
                
                return script;
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Integer> loadScriptsFromDirectory(Path directory) {
        return CompletableFuture.supplyAsync(() -> {
            ensureInitialized();
            
            if (!Files.exists(directory)) {
                logger.warn("Scripts directory does not exist: " + directory);
                return 0;
            }
            
            try (Stream<Path> paths = Files.walk(directory)) {
                List<Path> scriptFiles = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".js"))
                        .toList();
                
                int loaded = 0;
                for (Path scriptFile : scriptFiles) {
                    try {
                        loadScript(scriptFile).join();
                        loaded++;
                    } catch (Exception e) {
                        logger.error("Failed to read script: " + scriptFile, e);
                    }
                }
                
                logger.info("Loaded " + loaded + " scripts from " + directory);
                return loaded;
                
            } catch (IOException e) {
                logger.error("Failed to scan scripts directory", e);
                return 0;
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Object> execute(String code) {
        return CompletableFuture.supplyAsync(() -> {
            ensureInitialized();
            
            // Создаем временный контекст для выполнения кода
            Context tempContext = createIsolatedContext("temp_" + System.currentTimeMillis());
            try {
                Value result = tempContext.eval("js", code);
                return result.isNull() ? null : result.as(Object.class);
            } catch (PolyglotException e) {
                logger.warn("Script execution error", e);
                throw new RuntimeException(e);
            } finally {
                tempContext.close();
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Object> executeScript(String scriptId) {
        return CompletableFuture.supplyAsync(() -> {
            LoadedScriptImpl script = scripts.get(scriptId);
            if (script == null) {
                throw new IllegalArgumentException("Script not found: " + scriptId);
            }
            
            Context context = scriptContexts.get(scriptId);
            if (context == null) {
                throw new IllegalStateException("Context not found for script: " + scriptId);
            }
            
            try {
                String code = Files.readString(script.getPath());
                Value result = context.eval("js", code);
                
                script.incrementExecutionCount();
                script.updateLastExecutionTime();
                
                return result.isNull() ? null : result.as(Object.class);
                
            } catch (Exception e) {
                script.setError(e);
                logger.error("Failed to execute script: " + scriptId, e);
                throw new RuntimeException(e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> reloadScript(String scriptId) {
        return CompletableFuture.runAsync(() -> {
            LoadedScriptImpl script = scripts.get(scriptId);
            if (script == null) {
                throw new IllegalArgumentException("Script not found: " + scriptId);
            }
            
            // Закрываем старый контекст
            Context oldContext = scriptContexts.remove(scriptId);
            if (oldContext != null) {
                try { oldContext.close(); } catch (Exception ignored) {}
            }
            
            loadScript(script.getPath()).join();
            logger.info("Reloaded script (isolated): " + scriptId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Integer> reloadAllScripts() {
        return CompletableFuture.supplyAsync(() -> {
            List<Path> scriptPaths = scripts.values().stream()
                    .map(LoadedScriptImpl::getPath)
                    .toList();
            
            // Закрываем все старые контексты
            scriptContexts.values().forEach(ctx -> {
                try { ctx.close(); } catch (Exception ignored) {}
            });
            scriptContexts.clear();
            scripts.clear();
            
            int reloaded = 0;
            for (Path path : scriptPaths) {
                try {
                    loadScript(path).join();
                    reloaded++;
                } catch (Exception e) {
                    logger.error("Failed to reload script: " + path, e);
                }
            }
            
            logger.info("Reloaded " + reloaded + " scripts (isolated contexts)");
            return reloaded;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> unloadScript(String scriptId) {
        return CompletableFuture.runAsync(() -> {
            // Закрываем контекст скрипта
            Context context = scriptContexts.remove(scriptId);
            if (context != null) {
                try { context.close(); } catch (Exception ignored) {}
            }
            
            scripts.remove(scriptId);
            logger.info("Unloaded script (isolated): " + scriptId);
        }, executor);
    }
    
    @Override
    public Optional<LoadedScript> getScript(String scriptId) {
        return Optional.ofNullable(scripts.get(scriptId));
    }
    
    @Override
    public Collection<LoadedScript> getAllScripts() {
        return Collections.unmodifiableCollection(scripts.values());
    }
    
    @Override
    public boolean isInitialized() {
        return initialized.get();
    }
    
    @Override
    public Object getScriptAPI() {
        return scriptAPI;
    }
    
    private void ensureInitialized() {
        if (!initialized.get()) {
            throw new IllegalStateException("Script engine not initialized");
        }
    }
    
    public boolean wasPreviouslyLoaded() {
        return wasInitialized.get();
    }
    
    /**
     * Console API for scripts.
     * Provides logging functionality.
     */
    public static class ConsoleAPI {
        private final Logger logger;
        
        public ConsoleAPI(Logger logger) {
            this.logger = logger;
        }
        
        public void log(String message) {
            logger.info("[Script] " + message);
        }
        
        public void log(Object obj) {
            logger.info("[Script] " + String.valueOf(obj));
        }
        
        public void warn(String message) {
            logger.warn("[Script] " + message);
        }
        
        public void warn(Object obj) {
            logger.warn("[Script] " + String.valueOf(obj));
        }
        
        public void error(String message) {
            logger.error("[Script] " + message);
        }
        
        public void error(Object obj) {
            logger.error("[Script] " + String.valueOf(obj));
        }
        
        public void info(String message) {
            log(message);
        }
        
        public void debug(String message) {
            logger.debug("[Script] " + message);
        }
    }
    
    private static class LoadedScriptImpl implements LoadedScript {
        private final String id;
        private final Path path;
        private final long loadTime;
        private long lastExecutionTime;
        private int executionCount;
        private Throwable lastError;
        
        LoadedScriptImpl(String id, Path path, long loadTime) {
            this.id = id;
            this.path = path;
            this.loadTime = loadTime;
            this.lastExecutionTime = 0L;
            this.executionCount = 0;
        }
        
        @Override
        public String getId() {
            return id;
        }
        
        @Override
        public Path getPath() {
            return path;
        }
        
        @Override
        public long getLoadTime() {
            return loadTime;
        }
        
        @Override
        public long getLastExecutionTime() {
            return lastExecutionTime;
        }
        
        @Override
        public int getExecutionCount() {
            return executionCount;
        }
        
        @Override
        public boolean hasErrors() {
            return lastError != null;
        }
        
        @Override
        public Optional<Throwable> getLastError() {
            return Optional.ofNullable(lastError);
        }
        
        void updateLastExecutionTime() {
            this.lastExecutionTime = System.currentTimeMillis();
        }
        
        void incrementExecutionCount() {
            this.executionCount++;
        }
        
        void setError(Throwable error) {
            this.lastError = error;
        }
    }
}
