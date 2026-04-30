package com.scriptslab.core.script;

import com.scriptslab.api.script.ScriptEngine;
import org.graalvm.polyglot.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * GraalVM-based JavaScript engine implementation.
 * Thread-safe with sandboxing and reload resilience.
 */
public final class GraalScriptEngine implements ScriptEngine {
    
    private static final Logger STATIC_LOGGER = Logger.getLogger("ScriptEngine.Static");
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
    
    private Context jsContext;
    
    public GraalScriptEngine(Path scriptsDirectory, Object scriptAPI) {
        this.logger = Logger.getLogger("ScriptEngine");
        this.scriptsDirectory = scriptsDirectory;
        this.scriptAPI = scriptAPI;
        this.scripts = new ConcurrentHashMap<>();
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
                logger.warning("Script engine already initialized");
                return;
            }
            
            if (wasInitialized.get()) {
                logger.warning("Script engine was previously loaded - full reload required to reset");
                logger.info("Attempting to reset GraalVM state...");
                resetGraalVMState();
            }
            
            try {
                logger.info("Initializing GraalVM JavaScript engine...");
                
                Context context = null;
                Engine engine = null;
                
                synchronized (ENGINE_LOCK) {
                    if (sharedEngine == null || !engineLoaded.get()) {
                        logger.info("Creating new GraalVM engine...");
                        
                        engine = Engine.newBuilder()
                                .option("js.ecmascript-version", "2022")
                                .option("engine.WarnInterpreterOnly", "false") // Disable interpreter warning at engine level
                                .build();
                        
                        sharedEngine = engine;
                        engineLoaded.set(true);
                        
                        STATIC_LOGGER.info("Created new shared GraalVM engine");
                    } else {
                        engine = sharedEngine;
                        logger.info("Reusing existing GraalVM engine");
                    }
                }
                
                // Read sandbox setting from config
                boolean sandboxEnabled = false;
                try {
                    // scriptAPI is ScriptAPIImpl which holds the plugin reference
                    if (scriptAPI instanceof com.scriptslab.core.script.ScriptAPIImpl apiImpl) {
                        sandboxEnabled = apiImpl.getPlugin().getConfig()
                                .getBoolean("security.sandbox-enabled", false);
                    }
                } catch (Exception ignored) {}
                
                Context.Builder contextBuilder = Context.newBuilder("js")
                        .engine(engine)
                        .allowExperimentalOptions(true);
                
                if (sandboxEnabled) {
                    // Restricted mode - safe for untrusted scripts
                    contextBuilder
                            .allowHostAccess(org.graalvm.polyglot.HostAccess.EXPLICIT)
                            .allowHostClassLookup(className -> {
                                // Allow only safe Bukkit/Paper classes
                                return className.startsWith("org.bukkit.") ||
                                       className.startsWith("net.kyori.") ||
                                       className.startsWith("java.util.") ||
                                       className.startsWith("java.lang.String") ||
                                       className.startsWith("java.lang.Integer") ||
                                       className.startsWith("java.lang.Double") ||
                                       className.startsWith("java.lang.Boolean");
                            })
                            .allowIO(org.graalvm.polyglot.io.IOAccess.NONE); // No file access
                    
                    logger.info("Script engine initialized in SANDBOX mode (restricted)");
} else {
                    // Unrestricted mode - full access (use with caution!)
                    contextBuilder
                            .allowAllAccess(true)
                            .allowHostAccess(org.graalvm.polyglot.HostAccess.ALL)
                            .allowHostClassLookup(className -> true)
                            .allowIO(org.graalvm.polyglot.io.IOAccess.ALL)
                            .allowCreateThread(true)
                            .allowCreateProcess(true)
                            .allowEnvironmentAccess(org.graalvm.polyglot.EnvironmentAccess.INHERIT)
                            .allowPolyglotAccess(org.graalvm.polyglot.PolyglotAccess.ALL);

                    logger.warning("Script engine initialized in UNRESTRICTED mode - scripts have full access!");
                }
                
                context = contextBuilder.build();
                
                jsContext = context;
                
                ConsoleAPI consoleAPI = new ConsoleAPI(logger);
                
                // Expose main API objects
                jsContext.getBindings("js").putMember("API", scriptAPI);
                jsContext.getBindings("js").putMember("plugin", scriptAPI);
                
                // Expose Console/Logger
                jsContext.getBindings("js").putMember("console", consoleAPI);
                jsContext.getBindings("js").putMember("Console", consoleAPI);
                jsContext.getBindings("js").putMember("Logger", consoleAPI);
                
                // Expose individual API components as global objects
                jsContext.getBindings("js").putMember("Commands", scriptAPI);
                jsContext.getBindings("js").putMember("Events", scriptAPI);
                jsContext.getBindings("js").putMember("Players", scriptAPI);
                jsContext.getBindings("js").putMember("Server", scriptAPI);
                jsContext.getBindings("js").putMember("World", scriptAPI);
                jsContext.getBindings("js").putMember("Items", scriptAPI);

                // Expose subsystem APIs
                if (scriptAPI instanceof com.scriptslab.core.script.ScriptAPIImpl apiImpl) {
                    jsContext.getBindings("js").putMember("Scheduler", apiImpl.getScheduler());
                    jsContext.getBindings("js").putMember("Storage", apiImpl.getStorage());
                    // GUI is exposed after initGUI() is called
                    apiImpl.initGUI();
                    jsContext.getBindings("js").putMember("GUI", apiImpl.getGUI());
                } else {
                    jsContext.getBindings("js").putMember("Scheduler", scriptAPI);
                    jsContext.getBindings("js").putMember("Storage", scriptAPI);
                }
                
                scriptsDirectory.toFile().mkdirs();
                
                initialized.set(true);
                wasInitialized.set(true);
                logger.info("Script engine initialized successfully");
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to initialize script engine", e);
                throw new RuntimeException(e);
            }
    }
    
    private void resetGraalVMState() {
        logger.info("Resetting GraalVM static state...");
        
        synchronized (ENGINE_LOCK) {
            try {
                if (jsContext != null) {
                    jsContext.close();
                    jsContext = null;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error closing context", e);
            }
            
            try {
                if (sharedEngine != null) {
                    logger.info("Closing old shared engine...");
                    try {
                        sharedEngine.close();
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error closing shared engine", e);
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
            
            scripts.clear();
            
            try {
                if (jsContext != null) {
                    jsContext.close();
                    jsContext = null;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error closing context", e);
            }
            
            initialized.set(false);
            
            logger.info("Script engine shut down (shared engine preserved for reload)");
        });
    }
    
    @Override
    public CompletableFuture<LoadedScript> loadScript(Path scriptPath) {
        return CompletableFuture.supplyAsync(() -> {
            ensureInitialized();
            
            try {
                String scriptId = scriptPath.getFileName().toString()
                        .replace(".js", "");
                
                String code = Files.readString(scriptPath);
                Source source = Source.newBuilder("js", code, scriptId).build();
                
                Value result = jsContext.eval(source);
                
                LoadedScriptImpl script = new LoadedScriptImpl(
                        scriptId, scriptPath, System.currentTimeMillis());
                
                scripts.put(scriptId, script);
                script.incrementExecutionCount();
                
                logger.info("Loaded script: " + scriptId);
                return script;
                
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to read script: " + scriptPath, e);
                throw new RuntimeException(e);
            } catch (PolyglotException e) {
                logger.log(Level.SEVERE, "Script error in " + scriptPath, e);
                
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
                logger.warning("Scripts directory does not exist: " + directory);
                return 0;
            }
            
            try (Stream<Path> paths = Files.walk(directory)) {
                List<Path> scriptFiles = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".js"))
                        .toList();
                
                StringBuilder allCode = new StringBuilder();
                
                for (Path scriptFile : scriptFiles) {
                    try {
                        String code = Files.readString(scriptFile);
                        allCode.append(code).append("\n");
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Failed to read script: " + scriptFile, e);
                    }
                }
                
                if (allCode.length() > 0) {
                    Source source = Source.newBuilder("js", allCode.toString(), "all_scripts").build();
                    jsContext.eval(source);
                    logger.info("Loaded " + scriptFiles.size() + " scripts from " + directory);
                    return scriptFiles.size();
                }
                
                return 0;
                
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to scan scripts directory", e);
                return 0;
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Object> execute(String code) {
        return CompletableFuture.supplyAsync(() -> {
            ensureInitialized();
            
            // Синхронизируем доступ к Context для многопоточности
            synchronized (jsContext) {
                try {
                    Value result = jsContext.eval("js", code);
                    return result.isNull() ? null : result.as(Object.class);
                } catch (PolyglotException e) {
                    logger.log(Level.WARNING, "Script execution error", e);
                    throw new RuntimeException(e);
                }
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
            
            try {
                String code = Files.readString(script.getPath());
                Value result = jsContext.eval("js", code);
                
                script.incrementExecutionCount();
                script.updateLastExecutionTime();
                
                return result.isNull() ? null : result.as(Object.class);
                
            } catch (Exception e) {
                script.setError(e);
                logger.log(Level.SEVERE, "Failed to execute script: " + scriptId, e);
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
            
            loadScript(script.getPath()).join();
            logger.info("Reloaded script: " + scriptId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Integer> reloadAllScripts() {
        return CompletableFuture.supplyAsync(() -> {
            List<Path> scriptPaths = scripts.values().stream()
                    .map(LoadedScriptImpl::getPath)
                    .toList();
            
            scripts.clear();
            
            int reloaded = 0;
            for (Path path : scriptPaths) {
                try {
                    loadScript(path).join();
                    reloaded++;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed to reload script: " + path, e);
                }
            }
            
            logger.info("Reloaded " + reloaded + " scripts");
            return reloaded;
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> unloadScript(String scriptId) {
        return CompletableFuture.runAsync(() -> {
            scripts.remove(scriptId);
            logger.info("Unloaded script: " + scriptId);
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
            logger.warning("[Script] " + message);
        }
        
        public void warn(Object obj) {
            logger.warning("[Script] " + String.valueOf(obj));
        }
        
        public void error(String message) {
            logger.severe("[Script] " + message);
        }
        
        public void error(Object obj) {
            logger.severe("[Script] " + String.valueOf(obj));
        }
        
        public void info(String message) {
            log(message);
        }
        
        public void debug(String message) {
            logger.fine("[Script] " + message);
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
            this.lastExecutionTime = 0;
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