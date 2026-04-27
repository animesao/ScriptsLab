package com.scriptslab.api.module;

import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Base interface for all modules in the plugin system.
 * Modules are self-contained units of functionality that can be
 * loaded, enabled, disabled, and reloaded independently.
 * 
 * Thread-safety: Implementations must be thread-safe.
 */
public interface Module {
    
    /**
     * Gets the unique identifier of this module.
     * Must be lowercase, alphanumeric with underscores only.
     * 
     * @return module identifier
     */
    String getId();
    
    /**
     * Gets the human-readable name of this module.
     * 
     * @return module name
     */
    String getName();
    
    /**
     * Gets the module version.
     * 
     * @return version string
     */
    String getVersion();
    
    /**
     * Gets the module descriptor containing metadata.
     * 
     * @return module descriptor
     */
    ModuleDescriptor getDescriptor();
    
    /**
     * Gets the module's data folder.
     * 
     * @return path to module data folder
     */
    Path getDataFolder();
    
    /**
     * Gets the module's configuration.
     * 
     * @return configuration file
     */
    FileConfiguration getConfig();
    
    /**
     * Checks if the module is currently enabled.
     * 
     * @return true if enabled
     */
    boolean isEnabled();
    
    /**
     * Called when the module is loaded (before enable).
     * Use this for initialization that doesn't require other modules.
     * 
     * @return future that completes when loading is done
     */
    CompletableFuture<Void> onLoad();
    
    /**
     * Called when the module is enabled.
     * Register listeners, commands, items here.
     * 
     * @return future that completes when enabling is done
     */
    CompletableFuture<Void> onEnable();
    
    /**
     * Called when the module is disabled.
     * Clean up resources, unregister everything.
     * 
     * @return future that completes when disabling is done
     */
    CompletableFuture<Void> onDisable();
    
    /**
     * Called when the module is reloaded.
     * Default implementation: disable then enable.
     * 
     * @return future that completes when reloading is done
     */
    default CompletableFuture<Void> onReload() {
        return onDisable().thenCompose(v -> onEnable());
    }
    
    /**
     * Gets the module's logger name.
     * 
     * @return logger name
     */
    default String getLoggerName() {
        return "Module:" + getId();
    }
}
