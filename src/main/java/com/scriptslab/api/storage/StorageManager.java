package com.scriptslab.api.storage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Manager for storage providers.
 * Handles multiple storage backends.
 */
public interface StorageManager {
    
    /**
     * Initializes the storage manager.
     * 
     * @return future that completes when initialized
     */
    CompletableFuture<Void> initialize();
    
    /**
     * Shuts down the storage manager.
     * 
     * @return future that completes when shut down
     */
    CompletableFuture<Void> shutdown();
    
    /**
     * Gets the default storage provider.
     * 
     * @return default provider
     */
    StorageProvider getDefaultProvider();
    
    /**
     * Gets a storage provider by name.
     * 
     * @param name provider name
     * @return optional containing provider if found
     */
    Optional<StorageProvider> getProvider(String name);
    
    /**
     * Registers a storage provider.
     * 
     * @param provider provider to register
     * @return future that completes when registered
     */
    CompletableFuture<Void> registerProvider(StorageProvider provider);
    
    /**
     * Unregisters a storage provider.
     * 
     * @param name provider name
     * @return future that completes when unregistered
     */
    CompletableFuture<Void> unregisterProvider(String name);
}
