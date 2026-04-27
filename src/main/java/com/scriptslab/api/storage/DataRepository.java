package com.scriptslab.api.storage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Generic repository for data access.
 * Provides CRUD operations with caching.
 * 
 * @param <T> entity type
 * @param <ID> identifier type
 */
public interface DataRepository<T, ID> {
    
    /**
     * Saves an entity.
     * 
     * @param entity entity to save
     * @return future with saved entity
     */
    CompletableFuture<T> save(T entity);
    
    /**
     * Finds an entity by ID.
     * 
     * @param id entity identifier
     * @return future with optional containing entity if found
     */
    CompletableFuture<Optional<T>> findById(ID id);
    
    /**
     * Finds all entities.
     * 
     * @return future with list of all entities
     */
    CompletableFuture<List<T>> findAll();
    
    /**
     * Deletes an entity by ID.
     * 
     * @param id entity identifier
     * @return future that completes when deleted
     */
    CompletableFuture<Void> deleteById(ID id);
    
    /**
     * Checks if an entity exists.
     * 
     * @param id entity identifier
     * @return future with true if exists
     */
    CompletableFuture<Boolean> existsById(ID id);
    
    /**
     * Counts all entities.
     * 
     * @return future with entity count
     */
    CompletableFuture<Long> count();
    
    /**
     * Deletes all entities.
     * Use with caution!
     * 
     * @return future that completes when deleted
     */
    CompletableFuture<Void> deleteAll();
}
