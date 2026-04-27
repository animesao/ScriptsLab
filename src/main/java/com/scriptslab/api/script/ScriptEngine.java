package com.scriptslab.api.script;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Script engine for executing JavaScript code.
 * Thread-safe.
 */
public interface ScriptEngine {
    
    /**
     * Initializes the script engine.
     * 
     * @return future that completes when initialized
     */
    CompletableFuture<Void> initialize();
    
    /**
     * Shuts down the script engine.
     * 
     * @return future that completes when shut down
     */
    CompletableFuture<Void> shutdown();
    
    /**
     * Loads a script from a file.
     * 
     * @param scriptPath path to script file
     * @return future with loaded script
     */
    CompletableFuture<LoadedScript> loadScript(Path scriptPath);
    
    /**
     * Loads all scripts from a directory.
     * 
     * @param directory directory containing scripts
     * @return future with number of loaded scripts
     */
    CompletableFuture<Integer> loadScriptsFromDirectory(Path directory);
    
    /**
     * Executes JavaScript code.
     * 
     * @param code JavaScript code to execute
     * @return future with execution result
     */
    CompletableFuture<Object> execute(String code);
    
    /**
     * Executes a loaded script.
     * 
     * @param scriptId script identifier
     * @return future with execution result
     */
    CompletableFuture<Object> executeScript(String scriptId);
    
    /**
     * Reloads a script.
     * 
     * @param scriptId script identifier
     * @return future that completes when reloaded
     */
    CompletableFuture<Void> reloadScript(String scriptId);
    
    /**
     * Reloads all scripts.
     * 
     * @return future with number of reloaded scripts
     */
    CompletableFuture<Integer> reloadAllScripts();
    
    /**
     * Unloads a script.
     * 
     * @param scriptId script identifier
     * @return future that completes when unloaded
     */
    CompletableFuture<Void> unloadScript(String scriptId);
    
    /**
     * Gets a loaded script.
     * 
     * @param scriptId script identifier
     * @return optional containing script if found
     */
    Optional<LoadedScript> getScript(String scriptId);
    
    /**
     * Gets all loaded scripts.
     * 
     * @return collection of all scripts
     */
    Collection<LoadedScript> getAllScripts();
    
    /**
     * Checks if the engine is initialized.
     * 
     * @return true if initialized
     */
    boolean isInitialized();
    
    /**
     * Checks if the engine was previously loaded (before potential reload).
     * 
     * @return true if was loaded before
     */
    default boolean wasPreviouslyLoaded() {
        return false;
    }
    
    /**
     * Gets the script API object.
     * 
     * @return script API
     */
    Object getScriptAPI();
    
    /**
     * Represents a loaded script.
     */
    interface LoadedScript {
        
        /**
         * Gets the script ID.
         */
        String getId();
        
        /**
         * Gets the script file path.
         */
        Path getPath();
        
        /**
         * Gets the load timestamp.
         */
        long getLoadTime();
        
        /**
         * Gets the last execution timestamp.
         */
        long getLastExecutionTime();
        
        /**
         * Gets the number of times this script was executed.
         */
        int getExecutionCount();
        
        /**
         * Checks if the script has errors.
         */
        boolean hasErrors();
        
        /**
         * Gets the last error if any.
         */
        Optional<Throwable> getLastError();
    }
}
