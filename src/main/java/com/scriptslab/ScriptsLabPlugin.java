package com.scriptslab;

import com.scriptslab.api.command.CommandManager;
import com.scriptslab.api.event.EventBus;
import com.scriptslab.api.item.ItemManager;
import com.scriptslab.api.metrics.MetricsCollector;
import com.scriptslab.api.module.ModuleManager;
import com.scriptslab.api.scheduler.TaskScheduler;
import com.scriptslab.api.script.ScriptEngine;
import com.scriptslab.api.storage.StorageManager;
import com.scriptslab.core.command.CommandManagerImpl;
import com.scriptslab.core.config.ConfigurationManager;
import com.scriptslab.core.di.Container;
import com.scriptslab.core.event.EventBusImpl;
import com.scriptslab.core.item.ItemManagerImpl;
import com.scriptslab.core.metrics.MetricsCollectorImpl;
import com.scriptslab.core.module.ModuleManagerImpl;
import com.scriptslab.core.scheduler.TaskSchedulerImpl;
import com.scriptslab.core.script.GraalScriptEngine;
import com.scriptslab.core.script.ScriptAPIImpl;
import com.scriptslab.core.storage.StorageManagerImpl;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * ScriptsLab - Production-grade scriptable plugin framework.
 * 
 * Your laboratory for Minecraft scripting - experiment, create, and deploy
 * with confidence using our powerful modular architecture.
 * 
 * Features:
 * - JavaScript scripting with GraalVM
 * - Modular plugin system
 * - Custom items with abilities
 * - Event-driven architecture
 * - Storage abstraction
 * - Command framework
 * - GUI system
 * 
 * @author ScriptsLab Team
 * @version 1.0.0
 */
public final class ScriptsLabPlugin extends JavaPlugin {
    
    private static ScriptsLabPlugin instance;
    private static boolean hotReloadAttempted = false;
    private Container container;
    
    // Core services
    private ConfigurationManager configManager;
    private ModuleManager moduleManager;
    private EventBus eventBus;
    private TaskScheduler taskScheduler;
    private ItemManager itemManager;
    private ScriptEngine scriptEngine;
    private StorageManager storageManager;
    private CommandManager commandManager;
    private MetricsCollector metricsCollector;
    
    @Override
    public void onEnable() {
        if (instance != null && hotReloadAttempted) {
            getLogger().warning("================================================== ");
            getLogger().warning("  HOT RELOAD DETECTED - ScriptsLab requires full server restart!");
            getLogger().warning("  Please restart the server to reload ScriptsLab");
            getLogger().warning("  Using /restart or stop + start (not /reload)");
            getLogger().warning("================================================== ");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        if (instance == null) {
            hotReloadAttempted = false;
        }
        
        instance = this;
        long startTime = System.currentTimeMillis();
        
        printBanner();
        
        try {
            // Phase 1: Initialize DI container
            initializeContainer();
            
            // Phase 2: Load configuration
            initializeConfiguration();
            
            // Phase 3: Initialize core services
            initializeServices();
            
            // Phase 4: Register commands
            registerCommands();
            
            // Phase 5: Load modules (async)
            loadModulesAsync();
            
            // Phase 6: Load scripts (async)
            loadScriptsAsync();
            
            long loadTime = System.currentTimeMillis() - startTime;
            getLogger().info("╔═══════════════════════════════════════╗");
            getLogger().info("║   ScriptsLab - Ready!                 ║");
            getLogger().info("║   Load time: " + loadTime + "ms" + " ".repeat(Math.max(0, 23 - String.valueOf(loadTime).length())) + "║");
            getLogger().info("╚═══════════════════════════════════════╝");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Critical error during plugin initialization!", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Shutting down ScriptsLab...");
        
        try {
            // Shutdown in reverse order
            
            // 1. Unload scripts
            if (scriptEngine != null && scriptEngine.isInitialized()) {
                scriptEngine.shutdown().join();
            }
            
            // 2. Disable all modules
            if (moduleManager != null) {
                moduleManager.getEnabledModules().forEach(module -> {
                    try {
                        moduleManager.disableModule(module.getId()).join();
                    } catch (Exception e) {
                        getLogger().log(Level.WARNING, "Error disabling module: " + module.getId(), e);
                    }
                });
            }
            
            // 3. Shutdown storage
            if (storageManager != null) {
                storageManager.shutdown().join();
            }
            
            // 4. Cancel all tasks
            if (taskScheduler != null) {
                taskScheduler.cancelAllTasks();
            }
            
            // 5. Clear container
            if (container != null) {
                container.clear();
            }
            
            getLogger().info("ScriptsLab shut down successfully");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error during plugin shutdown", e);
        }
        
        instance = null;
    }
    
    /**
     * Prints the startup banner.
     */
    private void printBanner() {
        getLogger().info("╔═══════════════════════════════════════╗");
        getLogger().info("║                                       ║");
        getLogger().info("║         ScriptsLab v1.0.0             ║");
        getLogger().info("║   Your Minecraft Scripting Lab        ║");
        getLogger().info("║                                       ║");
        getLogger().info("╚═══════════════════════════════════════╝");
    }
    
    /**
     * Initializes the dependency injection container.
     */
    private void initializeContainer() {
        getLogger().info("→ Initializing DI container...");
        container = Container.getInstance();
        
        // Register plugin instance
        container.registerSingleton(JavaPlugin.class, this);
        container.registerSingleton(ScriptsLabPlugin.class, this);
        
        getLogger().info("✓ DI container initialized");
    }
    
    /**
     * Initializes configuration system.
     */
    private void initializeConfiguration() {
        getLogger().info("→ Loading configuration...");
        
        saveDefaultConfig();
        configManager = new ConfigurationManager(this);
        container.registerSingleton(ConfigurationManager.class, configManager);
        
        getLogger().info("✓ Configuration loaded");
    }
    
    /**
     * Initializes core services and registers them in the container.
     */
    private void initializeServices() {
        getLogger().info("→ Initializing core services...");
        
        // Metrics Collector
        metricsCollector = new MetricsCollectorImpl();
        container.registerSingleton(MetricsCollector.class, metricsCollector);
        getLogger().info("  ✓ Metrics Collector");
        
        // Event Bus
        eventBus = new EventBusImpl();
        container.registerSingleton(EventBus.class, eventBus);
        getLogger().info("  ✓ Event Bus");
        
        // Task Scheduler
        taskScheduler = new TaskSchedulerImpl(this);
        container.registerSingleton(TaskScheduler.class, taskScheduler);
        getLogger().info("  ✓ Task Scheduler");
        
        // Storage Manager
        storageManager = new StorageManagerImpl(this);
        storageManager.initialize().join();
        container.registerSingleton(StorageManager.class, storageManager);
        getLogger().info("  ✓ Storage Manager");
        
        // Item Manager
        itemManager = new ItemManagerImpl(this);
        container.registerSingleton(ItemManager.class, itemManager);
        getLogger().info("  ✓ Item Manager");
        
        // Command Manager
        commandManager = new CommandManagerImpl(this);
        container.registerSingleton(CommandManager.class, commandManager);
        getLogger().info("  ✓ Command Manager");
        
        // Module Manager
        Path modulesDir = getDataFolder().toPath().resolve("modules");
        moduleManager = new ModuleManagerImpl(modulesDir);
        container.registerSingleton(ModuleManager.class, moduleManager);
        getLogger().info("  ✓ Module Manager");
        
        // Script Engine
        Path scriptsDir = getDataFolder().toPath().resolve("scripts");
        ScriptAPIImpl scriptAPI = new ScriptAPIImpl(this);
        scriptEngine = new GraalScriptEngine(scriptsDir, scriptAPI);
        scriptEngine.initialize().join();
        container.registerSingleton(ScriptEngine.class, scriptEngine);
        getLogger().info("  ✓ Script Engine");
        
        // Start periodic cleanup task for item cooldowns
        taskScheduler.runTaskTimer(
                () -> ((ItemManagerImpl) itemManager).cleanupCooldowns(),
                60, 60, java.util.concurrent.TimeUnit.SECONDS
        );
        
        getLogger().info("✓ Core services initialized");
    }
    
    /**
     * Registers plugin commands.
     */
    private void registerCommands() {
        getLogger().info("→ Registering commands...");
        
        commandManager.registerCommands();
        
        getLogger().info("✓ Commands registered");
    }
    
    /**
     * Loads all modules asynchronously.
     */
    private void loadModulesAsync() {
        getLogger().info("→ Loading modules...");
        
        moduleManager.loadAllModules()
                .thenAccept(count -> {
                    getLogger().info("✓ Loaded " + count + " modules");
                    
                    // Enable all loaded modules
                    moduleManager.getModules().forEach(module -> {
                        try {
                            moduleManager.enableModule(module.getId()).join();
                        } catch (Exception e) {
                            getLogger().log(Level.SEVERE, "Failed to enable module: " + module.getId(), e);
                        }
                    });
                })
                .exceptionally(ex -> {
                    getLogger().log(Level.SEVERE, "Failed to load modules", ex);
                    return null;
                });
    }
    
    /**
     * Loads all scripts asynchronously.
     */
    private void loadScriptsAsync() {
        getLogger().info("→ Loading scripts...");
        
        Path scriptsDir = getDataFolder().toPath().resolve("scripts");
        
        scriptEngine.loadScriptsFromDirectory(scriptsDir)
                .thenAccept(count -> getLogger().info("✓ Loaded " + count + " scripts"))
                .exceptionally(ex -> {
                    getLogger().log(Level.SEVERE, "Failed to load scripts", ex);
                    return null;
                });
    }
    
    /**
     * Reloads the entire plugin.
     * 
     * @return future that completes when reloaded
     */
    public CompletableFuture<Void> reload() {
        return CompletableFuture.runAsync(() -> {
            getLogger().info("Reloading ScriptsLab...");
            
            try {
                // Reload configuration
                reloadConfig();
                configManager.reload();
                
                // Reload scripts
                scriptEngine.reloadAllScripts().join();
                
                // Reload modules
                moduleManager.getModules().forEach(module -> {
                    try {
                        moduleManager.reloadModule(module.getId()).join();
                    } catch (Exception e) {
                        getLogger().log(Level.WARNING, "Failed to reload module: " + module.getId(), e);
                    }
                });
                
                getLogger().info("ScriptsLab reloaded successfully");
                
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Error during plugin reload", e);
                throw new RuntimeException(e);
            }
        });
    }
    
    // === Getters ===
    
    public static ScriptsLabPlugin getInstance() {
        return instance;
    }
    
    public static void setHotReloadAttempted(boolean attempted) {
        hotReloadAttempted = attempted;
        if (instance != null) {
            instance.getLogger().warning("Hot reload flag set - next enable will require full restart");
        }
    }
    
    public Container getContainer() {
        return container;
    }
    
    public ConfigurationManager getConfigManager() {
        return configManager;
    }
    
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    public EventBus getEventBus() {
        return eventBus;
    }
    
    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }
    
    public ItemManager getItemManager() {
        return itemManager;
    }
    
    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }
    
    public StorageManager getStorageManager() {
        return storageManager;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public MetricsCollector getMetricsCollector() {
        return metricsCollector;
    }
    
    /**
     * Gets a service from the DI container.
     * 
     * @param type service type
     * @param <T> type parameter
     * @return service instance
     */
    public <T> T getService(Class<T> type) {
        return container.resolveOrThrow(type);
    }
}
