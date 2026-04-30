package com.scriptslab.api.module;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.scriptslab.api.module.ModuleDescriptor;

/**
 * Manager for loading, enabling, disabling, and managing modules.
 * Thread-safe.
 */
public interface ModuleManager {
    
    /**
     * Loads all modules from the modules directory.
     * 
     * @return future with number of loaded modules
     */
    CompletableFuture<Integer> loadAllModules();
    
    /**
     * Loads a specific module from a path.
     * 
     * @param modulePath path to module directory or JAR
     * @return future with loaded module
     */
    CompletableFuture<Module> loadModule(Path modulePath);
    
    /**
     * Enables a module by ID.
     * 
     * @param moduleId module identifier
     * @return future that completes when enabled
     */
    CompletableFuture<Void> enableModule(String moduleId);
    
    /**
     * Disables a module by ID.
     * 
     * @param moduleId module identifier
     * @return future that completes when disabled
     */
    CompletableFuture<Void> disableModule(String moduleId);
    
    /**
     * Reloads a module by ID.
     * 
     * @param moduleId module identifier
     * @return future that completes when reloaded
     */
    CompletableFuture<Void> reloadModule(String moduleId);
    
    /**
     * Unloads a module completely.
     * 
     * @param moduleId module identifier
     * @return future that completes when unloaded
     */
    CompletableFuture<Void> unloadModule(String moduleId);
    
    /**
     * Gets a module by ID.
     * 
     * @param moduleId module identifier
     * @return optional containing module if found
     */
    Optional<Module> getModule(String moduleId);
    
    /**
     * Gets all loaded modules.
     * 
     * @return collection of all modules
     */
    Collection<Module> getModules();
    
    /**
     * Gets all enabled modules.
     * 
     * @return collection of enabled modules
     */
    Collection<Module> getEnabledModules();
    
    /**
     * Checks if a module is loaded.
     * 
     * @param moduleId module identifier
     * @return true if loaded
     */
    boolean isLoaded(String moduleId);
    
    /**
     * Checks if a module is enabled.
     * 
     * @param moduleId module identifier
     * @return true if enabled
     */
    boolean isEnabled(String moduleId);
    
    /**
     * Gets the modules directory.
     * 
     * @return path to modules directory
     */
    Path getModulesDirectory();
    
    /**
     * Resolves module load order based on dependencies.
     * 
     * @param modules modules to resolve
     * @return ordered list of module IDs
     * @throws IllegalStateException if circular dependency detected
     */
    java.util.List<String> resolveLoadOrder(Collection<ModuleDescriptor> modules);
}
