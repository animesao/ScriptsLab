package com.scriptslab.core.item;

import com.scriptslab.api.item.CustomItem;
import com.scriptslab.api.item.ItemAbility;
import com.scriptslab.api.item.ItemManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Thread-safe implementation of ItemManager.
 */
public final class ItemManagerImpl implements ItemManager {
    
    private final Plugin plugin;
    private final Logger logger;
    private final Map<String, CustomItem> items;
    private final Map<String, ItemAbility> abilities;
    private final Map<UUID, Map<String, Long>> cooldowns;
    private final NamespacedKey itemIdKey;
    
    public ItemManagerImpl(Plugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger("ItemManager");
        this.items = new ConcurrentHashMap<>();
        this.abilities = new ConcurrentHashMap<>();
        this.cooldowns = new ConcurrentHashMap<>();
        this.itemIdKey = new NamespacedKey(plugin, "custom_item_id");
    }
    
    @Override
    public CompletableFuture<Void> registerItem(CustomItem item) {
        return CompletableFuture.runAsync(() -> {
            if (item == null) {
                throw new IllegalArgumentException("Item cannot be null");
            }
            
            items.put(item.getId(), item);
            
            // Register abilities
            for (ItemAbility ability : item.getAbilities()) {
                abilities.put(ability.getId(), ability);
            }
            
            logger.fine("Registered item: " + item.getId());
        });
    }
    
    @Override
    public CompletableFuture<Void> unregisterItem(String itemId) {
        return CompletableFuture.runAsync(() -> {
            CustomItem item = items.remove(itemId);
            if (item != null) {
                // Unregister abilities
                for (ItemAbility ability : item.getAbilities()) {
                    abilities.remove(ability.getId());
                }
                logger.fine("Unregistered item: " + itemId);
            }
        });
    }
    
    @Override
    public Optional<CustomItem> getItem(String itemId) {
        return Optional.ofNullable(items.get(itemId));
    }
    
    @Override
    public Collection<CustomItem> getAllItems() {
        return Collections.unmodifiableCollection(items.values());
    }
    
    @Override
    public boolean isCustomItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        return meta.getPersistentDataContainer().has(itemIdKey, PersistentDataType.STRING);
    }
    
    @Override
    public Optional<String> getCustomItemId(ItemStack itemStack) {
        if (!isCustomItem(itemStack)) {
            return Optional.empty();
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        String id = meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
        return Optional.ofNullable(id);
    }
    
    @Override
    public Optional<CustomItem> getCustomItem(ItemStack itemStack) {
        return getCustomItemId(itemStack)
                .flatMap(this::getItem);
    }
    
    @Override
    public Optional<ItemStack> createItemStack(String itemId, int amount) {
        CustomItem item = items.get(itemId);
        if (item == null) {
            return Optional.empty();
        }
        
        ItemStack itemStack = item.toItemStack(amount);
        
        // Add custom item ID to NBT
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, itemId);
            itemStack.setItemMeta(meta);
        }
        
        return Optional.of(itemStack);
    }
    
    @Override
    public CompletableFuture<Void> registerAbility(ItemAbility ability) {
        return CompletableFuture.runAsync(() -> {
            if (ability == null) {
                throw new IllegalArgumentException("Ability cannot be null");
            }
            
            abilities.put(ability.getId(), ability);
            logger.fine("Registered ability: " + ability.getId());
        });
    }
    
    @Override
    public Optional<ItemAbility> getAbility(String abilityId) {
        return Optional.ofNullable(abilities.get(abilityId));
    }
    
    @Override
    public boolean canUseAbility(UUID playerId, String abilityId) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) {
            return true;
        }
        
        Long cooldownEnd = playerCooldowns.get(abilityId);
        if (cooldownEnd == null) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        return currentTime >= cooldownEnd;
    }
    
    @Override
    public void setCooldown(UUID playerId, String abilityId, long cooldownTicks) {
        long cooldownMillis = cooldownTicks * 50; // 1 tick = 50ms
        long cooldownEnd = System.currentTimeMillis() + cooldownMillis;
        
        cooldowns.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                .put(abilityId, cooldownEnd);
    }
    
    @Override
    public long getRemainingCooldown(UUID playerId, String abilityId) {
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (playerCooldowns == null) {
            return 0;
        }
        
        Long cooldownEnd = playerCooldowns.get(abilityId);
        if (cooldownEnd == null) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long remaining = cooldownEnd - currentTime;
        
        if (remaining <= 0) {
            playerCooldowns.remove(abilityId);
            return 0;
        }
        
        return remaining / 50; // Convert to ticks
    }
    
    /**
     * Cleans up expired cooldowns.
     * Should be called periodically.
     */
    public void cleanupCooldowns() {
        long currentTime = System.currentTimeMillis();
        
        cooldowns.values().forEach(playerCooldowns -> 
            playerCooldowns.entrySet().removeIf(entry -> entry.getValue() <= currentTime)
        );
        
        // Remove empty player cooldown maps
        cooldowns.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
