package com.scriptslab.api.storage;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract storage provider interface.
 * Implementations must be thread-safe.
 */
public interface StorageProvider {
    
    /**
     * Gets the provider name.
     * 
     * @return provider name (e.g., "yaml", "json", "sqlite")
     */
    String getName();
    
    /**
     * Initializes the storage provider.
     * 
     * @return future that completes when initialized
     */
    CompletableFuture<Void> initialize();
    
    /**
     * Shuts down the storage provider.
     * 
     * @return future that completes when shut down
     */
    CompletableFuture<Void> shutdown();
    
    /**
     * Saves a value.
     * 
     * @param key storage key
     * @param value value to save
     * @return future that completes when saved
     */
    CompletableFuture<Void> save(String key, Object value);
    
    /**
     * Saves multiple values in a batch.
     * 
     * @param data map of key-value pairs
     * @return future that completes when saved
     */
    CompletableFuture<Void> saveBatch(Map<String, Object> data);
    
    /**
     * Loads a value.
     * 
     * @param key storage key
     * @param type expected type
     * @param <T> type parameter
     * @return future with optional containing value if found
     */
    <T> CompletableFuture<Optional<T>> load(String key, Class<T> type);
    
    /**
     * Loads multiple values.
     * 
     * @param keys keys to load
     * @return future with map of found values
     */
    CompletableFuture<Map<String, Object>> loadBatch(Set<String> keys);
    
    /**
     * Deletes a value.
     * 
     * @param key storage key
     * @return future that completes when deleted
     */
    CompletableFuture<Void> delete(String key);
    
    /**
     * Checks if a key exists.
     * 
     * @param key storage key
     * @return future with true if exists
     */
    CompletableFuture<Boolean> exists(String key);
    
    /**
     * Gets all keys.
     * 
     * @return future with set of all keys
     */
    CompletableFuture<Set<String>> getAllKeys();
    
    /**
     * Clears all data.
     * Use with caution!
     * 
     * @return future that completes when cleared
     */
    CompletableFuture<Void> clear();
    
    /**
     * Checks if the provider is initialized.
     * 
     * @return true if initialized
     */
    boolean isInitialized();
}
