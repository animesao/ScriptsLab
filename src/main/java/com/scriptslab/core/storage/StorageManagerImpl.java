package com.scriptslab.core.storage;

import com.scriptslab.ScriptsLabPlugin;
import com.scriptslab.api.storage.StorageManager;
import com.scriptslab.api.storage.StorageProvider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of StorageManager.
 */
public final class StorageManagerImpl implements StorageManager {
    
    private final ScriptsLabPlugin plugin;
    private final Map<String, StorageProvider> providers;
    private StorageProvider defaultProvider;
    
    public StorageManagerImpl(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
        this.providers = new ConcurrentHashMap<>();
    }
    
    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            plugin.getLogger().info("Initializing storage system...");
            
            // Register YAML provider as default
            YamlStorageProvider yamlProvider = new YamlStorageProvider(
                    plugin.getDataFolder().toPath().resolve("data"));
            
            registerProvider(yamlProvider).join();
            yamlProvider.initialize().join();
            
            defaultProvider = yamlProvider;
            
            plugin.getLogger().info("Storage system initialized with YAML provider");
        });
    }
    
    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            plugin.getLogger().info("Shutting down storage system...");
            
            for (StorageProvider provider : providers.values()) {
                try {
                    provider.shutdown().join();
                } catch (Exception e) {
                    plugin.getLogger().warning("Error shutting down provider: " + provider.getName());
                }
            }
            
            providers.clear();
            plugin.getLogger().info("Storage system shut down");
        });
    }
    
    @Override
    public StorageProvider getDefaultProvider() {
        return defaultProvider;
    }
    
    @Override
    public Optional<StorageProvider> getProvider(String name) {
        return Optional.ofNullable(providers.get(name));
    }
    
    @Override
    public CompletableFuture<Void> registerProvider(StorageProvider provider) {
        return CompletableFuture.runAsync(() -> {
            providers.put(provider.getName(), provider);
            plugin.getLogger().fine("Registered storage provider: " + provider.getName());
        });
    }
    
    @Override
    public CompletableFuture<Void> unregisterProvider(String name) {
        return CompletableFuture.runAsync(() -> {
            StorageProvider provider = providers.remove(name);
            
            if (provider != null) {
                provider.shutdown().join();
                plugin.getLogger().fine("Unregistered storage provider: " + name);
            }
        });
    }
}
