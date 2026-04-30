package com.scriptslab.core.storage;

import com.scriptslab.api.storage.StorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * YAML-based storage provider implementation.
 * Thread-safe with caching.
 */
public final class YamlStorageProvider implements StorageProvider {
    
    private final Path dataDirectory;
    private final Map<String, Object> cache;
    private final Map<String, YamlConfiguration> fileCache;
    private volatile boolean initialized;
    
    public YamlStorageProvider(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.cache = new ConcurrentHashMap<>();
        this.fileCache = new ConcurrentHashMap<>();
        this.initialized = false;
    }
    
    @Override
    public String getName() {
        return "yaml";
    }
    
    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            dataDirectory.toFile().mkdirs();
            initialized = true;
        });
    }
    
    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            // Save all cached data
            for (Map.Entry<String, YamlConfiguration> entry : fileCache.entrySet()) {
                try {
                    File file = getFile(entry.getKey());
                    entry.getValue().save(file);
                } catch (Exception e) {
                    // Log error but continue
                }
            }
            
            cache.clear();
            fileCache.clear();
            initialized = false;
        });
    }
    
    @Override
    public CompletableFuture<Void> save(String key, Object value) {
        return CompletableFuture.runAsync(() -> {
            ensureInitialized();
            
            cache.put(key, value);
            
            // Determine file and path
            String[] parts = key.split("\\.", 2);
            String fileName = parts[0];
            String path = parts.length > 1 ? parts[1] : "value";
            
            YamlConfiguration yaml = getOrCreateYaml(fileName);
            yaml.set(path, value);
            
            // Save to disk
            try {
                yaml.save(getFile(fileName));
            } catch (Exception e) {
                throw new RuntimeException("Failed to save: " + key, e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> saveBatch(Map<String, Object> data) {
        return CompletableFuture.runAsync(() -> {
            ensureInitialized();
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                save(entry.getKey(), entry.getValue()).join();
            }
        });
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<Optional<T>> load(String key, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            ensureInitialized();
            
            // Check cache first
            if (cache.containsKey(key)) {
                return Optional.of((T) cache.get(key));
            }
            
            // Load from file
            String[] parts = key.split("\\.", 2);
            String fileName = parts[0];
            String path = parts.length > 1 ? parts[1] : "value";
            
            YamlConfiguration yaml = getOrCreateYaml(fileName);
            Object value = yaml.get(path);
            
            if (value != null) {
                cache.put(key, value);
                return Optional.of((T) value);
            }
            
            return Optional.empty();
        });
    }
    
    @Override
    public CompletableFuture<Map<String, Object>> loadBatch(Set<String> keys) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> result = new HashMap<>();
            
            for (String key : keys) {
                load(key, Object.class).join().ifPresent(value -> result.put(key, value));
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<Void> delete(String key) {
        return CompletableFuture.runAsync(() -> {
            ensureInitialized();
            
            cache.remove(key);
            
            String[] parts = key.split("\\.", 2);
            String fileName = parts[0];
            String path = parts.length > 1 ? parts[1] : "value";
            
            YamlConfiguration yaml = getOrCreateYaml(fileName);
            yaml.set(path, null);
            
            try {
                yaml.save(getFile(fileName));
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete: " + key, e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> exists(String key) {
        return CompletableFuture.supplyAsync(() -> {
            ensureInitialized();
            
            if (cache.containsKey(key)) {
                return true;
            }
            
            String[] parts = key.split("\\.", 2);
            String fileName = parts[0];
            String path = parts.length > 1 ? parts[1] : "value";
            
            YamlConfiguration yaml = getOrCreateYaml(fileName);
            return yaml.contains(path);
        });
    }
    
    @Override
    public CompletableFuture<Set<String>> getAllKeys() {
        return CompletableFuture.supplyAsync(() -> {
            ensureInitialized();
            return new HashSet<>(cache.keySet());
        });
    }
    
    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.runAsync(() -> {
            ensureInitialized();
            
            cache.clear();
            fileCache.clear();
            
            // Delete all files
            File[] files = dataDirectory.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".yml")) {
                        file.delete();
                    }
                }
            }
        });
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    private void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Storage provider not initialized");
        }
    }
    
    private File getFile(String fileName) {
        return dataDirectory.resolve(fileName + ".yml").toFile();
    }
    
    private YamlConfiguration getOrCreateYaml(String fileName) {
        return fileCache.computeIfAbsent(fileName, name -> {
            File file = getFile(name);
            
            if (file.exists()) {
                return YamlConfiguration.loadConfiguration(file);
            } else {
                return new YamlConfiguration();
            }
        });
    }
}
