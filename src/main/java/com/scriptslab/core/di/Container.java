package com.scriptslab.core.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Lightweight dependency injection container.
 * Thread-safe singleton implementation.
 */
public final class Container {
    
    private static volatile Container instance;
    private static final Object LOCK = new Object();
    
    private final Map<Class<?>, Object> singletons;
    private final Map<Class<?>, Supplier<?>> factories;
    private final Logger logger;
    
    private Container() {
        this.singletons = new ConcurrentHashMap<>();
        this.factories = new ConcurrentHashMap<>();
        this.logger = LoggerFactory.getLogger("DI-Container");
    }
    
    /**
     * Gets the singleton instance of the container.
     * Double-checked locking for thread safety.
     * 
     * @return container instance
     */
    public static Container getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new Container();
                }
            }
        }
        return instance;
    }
    
    /**
     * Registers a singleton instance.
     * 
     * @param type service type
     * @param instance service instance
     * @param <T> type parameter
     */
    public <T> void registerSingleton(Class<T> type, T instance) {
        if (type == null || instance == null) {
            throw new IllegalArgumentException("Type and instance cannot be null");
        }
        
        singletons.put(type, instance);
        logger.debug("Registered singleton: " + type.getSimpleName());
    }
    
    /**
     * Registers a factory for creating instances.
     * 
     * @param type service type
     * @param factory factory supplier
     * @param <T> type parameter
     */
    public <T> void registerFactory(Class<T> type, Supplier<T> factory) {
        if (type == null || factory == null) {
            throw new IllegalArgumentException("Type and factory cannot be null");
        }
        
        factories.put(type, factory);
        logger.debug("Registered factory: " + type.getSimpleName());
    }
    
    /**
     * Registers a lazy singleton.
     * Instance will be created only when first requested.
     * 
     * @param type service type
     * @param factory factory supplier
     * @param <T> type parameter
     */
    public <T> void registerLazySingleton(Class<T> type, Supplier<T> factory) {
        if (type == null || factory == null) {
            throw new IllegalArgumentException("Type and factory cannot be null");
        }
        
        LazySingleton<T> lazySingleton = new LazySingleton<>(factory);
        singletons.put(type, lazySingleton);
        logger.debug("Registered lazy singleton: " + type.getSimpleName());
    }
    
    /**
     * Internal class for lazy initialization.
     */
    private static class LazySingleton<T> {
        private volatile T instance;
        private final Supplier<T> factory;
        private final Object lock = new Object();
        
        LazySingleton(Supplier<T> factory) {
            this.factory = factory;
        }
        
        @SuppressWarnings("unchecked")
        T get() {
            if (instance == null) {
                synchronized (lock) {
                    if (instance == null) {
                        instance = factory.get();
                    }
                }
            }
            return instance;
        }
    }
    
    /**
     * Resolves a service by type.
     * First checks singletons, then factories.
     * Handles LazySingleton for lazy initialization.
     * 
     * @param type service type
     * @param <T> type parameter
     * @return optional containing service if found
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> resolve(Class<T> type) {
        if (type == null) {
            return Optional.empty();
        }
        
        Object singleton = singletons.get(type);
        if (singleton != null) {
            if (singleton instanceof LazySingleton) {
                return Optional.of((T) ((LazySingleton<T>) singleton).get());
            }
            return Optional.of((T) singleton);
        }
        
        Supplier<?> factory = factories.get(type);
        if (factory != null) {
            return Optional.of((T) factory.get());
        }
        
        return Optional.empty();
    }
    
    /**
     * Resolves a service or throws exception if not found.
     * 
     * @param type service type
     * @param <T> type parameter
     * @return service instance
     * @throws IllegalStateException if service not found
     */
    public <T> T resolveOrThrow(Class<T> type) {
        return resolve(type)
                .orElseThrow(() -> new IllegalStateException(
                        "Service not registered: " + type.getSimpleName()));
    }
    
    /**
     * Checks if a service is registered.
     * 
     * @param type service type
     * @return true if registered
     */
    public boolean isRegistered(Class<?> type) {
        return singletons.containsKey(type) || factories.containsKey(type);
    }
    
    /**
     * Unregisters a service.
     * 
     * @param type service type
     */
    public void unregister(Class<?> type) {
        singletons.remove(type);
        factories.remove(type);
        logger.debug("Unregistered: " + type.getSimpleName());
    }
    
    /**
     * Clears all registrations.
     * Use with caution!
     */
    public void clear() {
        singletons.clear();
        factories.clear();
        logger.info("Container cleared");
    }
    
    /**
     * Gets the number of registered services.
     * 
     * @return service count
     */
    public int size() {
        return singletons.size() + factories.size();
    }
}
