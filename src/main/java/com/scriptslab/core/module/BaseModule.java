package com.scriptslab.core.module;

import com.scriptslab.api.module.Module;
import com.scriptslab.api.module.ModuleDescriptor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Abstract base implementation of Module.
 * Provides common functionality for all modules.
 */
public abstract class BaseModule implements Module {
    
    protected final ModuleDescriptor descriptor;
    protected final Path dataFolder;
    protected final Logger logger;
    protected final AtomicBoolean enabled;
    
    private FileConfiguration config;
    
    protected BaseModule(ModuleDescriptor descriptor, Path dataFolder) {
        this.descriptor = descriptor;
        this.dataFolder = dataFolder;
        this.logger = Logger.getLogger(getLoggerName());
        this.enabled = new AtomicBoolean(false);
        
        // Create data folder
        dataFolder.toFile().mkdirs();
    }
    
    @Override
    public String getId() {
        return descriptor.id();
    }
    
    @Override
    public String getName() {
        return descriptor.name();
    }
    
    @Override
    public String getVersion() {
        return descriptor.version();
    }
    
    @Override
    public ModuleDescriptor getDescriptor() {
        return descriptor;
    }
    
    @Override
    public Path getDataFolder() {
        return dataFolder;
    }
    
    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled.get();
    }
    
    /**
     * Loads the module configuration.
     */
    protected void loadConfig() {
        File configFile = dataFolder.resolve("config.yml").toFile();
        
        if (!configFile.exists()) {
            // Create default config
            saveDefaultConfig();
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    /**
     * Saves the default configuration.
     * Override to provide default config values.
     */
    protected void saveDefaultConfig() {
        File configFile = dataFolder.resolve("config.yml").toFile();
        
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                FileConfiguration defaultConfig = new YamlConfiguration();
                provideDefaultConfig(defaultConfig);
                defaultConfig.save(configFile);
            } catch (Exception e) {
                logger.warning("Failed to create default config: " + e.getMessage());
            }
        }
    }
    
    /**
     * Override to provide default configuration values.
     * 
     * @param config configuration to populate
     */
    protected void provideDefaultConfig(FileConfiguration config) {
        config.set("enabled", true);
    }
    
    /**
     * Saves the current configuration.
     */
    protected void saveConfig() {
        if (config != null) {
            try {
                File configFile = dataFolder.resolve("config.yml").toFile();
                config.save(configFile);
            } catch (Exception e) {
                logger.warning("Failed to save config: " + e.getMessage());
            }
        }
    }
    
    /**
     * Reloads the configuration.
     */
    protected void reloadConfig() {
        config = null;
        loadConfig();
    }
    
    /**
     * Logs an info message.
     */
    protected void log(String message) {
        logger.info(message);
    }
    
    /**
     * Logs a warning message.
     */
    protected void warn(String message) {
        logger.warning(message);
    }
    
    /**
     * Logs a severe message.
     */
    protected void error(String message) {
        logger.severe(message);
    }
    
    /**
     * Logs an error with exception.
     */
    protected void error(String message, Throwable throwable) {
        logger.severe(message);
        logger.severe(throwable.getMessage());
    }
    
    @Override
    public CompletableFuture<Void> onLoad() {
        return CompletableFuture.runAsync(() -> {
            log("Loading module...");
            loadConfig();
        });
    }
    
    @Override
    public CompletableFuture<Void> onEnable() {
        return CompletableFuture.runAsync(() -> {
            log("Enabling module...");
            enabled.set(true);
        });
    }
    
    @Override
    public CompletableFuture<Void> onDisable() {
        return CompletableFuture.runAsync(() -> {
            log("Disabling module...");
            enabled.set(false);
        });
    }
    
    @Override
    public String toString() {
        return String.format("Module{id=%s, name=%s, version=%s, enabled=%s}",
                getId(), getName(), getVersion(), isEnabled());
    }
}
