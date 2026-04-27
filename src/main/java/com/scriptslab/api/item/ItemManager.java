package com.scriptslab.api.item;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Manager for custom items.
 * Thread-safe.
 */
public interface ItemManager {
    
    /**
     * Registers a custom item.
     * 
     * @param item custom item to register
     * @return future that completes when registered
     */
    CompletableFuture<Void> registerItem(CustomItem item);
    
    /**
     * Unregisters a custom item.
     * 
     * @param itemId item identifier
     * @return future that completes when unregistered
     */
    CompletableFuture<Void> unregisterItem(String itemId);
    
    /**
     * Gets a custom item by ID.
     * 
     * @param itemId item identifier
     * @return optional containing item if found
     */
    Optional<CustomItem> getItem(String itemId);
    
    /**
     * Gets all registered custom items.
     * 
     * @return collection of all items
     */
    Collection<CustomItem> getAllItems();
    
    /**
     * Checks if an ItemStack is a custom item.
     * 
     * @param itemStack item to check
     * @return true if custom item
     */
    boolean isCustomItem(ItemStack itemStack);
    
    /**
     * Gets the custom item ID from an ItemStack.
     * 
     * @param itemStack item to check
     * @return optional containing ID if custom item
     */
    Optional<String> getCustomItemId(ItemStack itemStack);
    
    /**
     * Gets the custom item from an ItemStack.
     * 
     * @param itemStack item to check
     * @return optional containing custom item if found
     */
    Optional<CustomItem> getCustomItem(ItemStack itemStack);
    
    /**
     * Creates an ItemStack from a custom item ID.
     * 
     * @param itemId item identifier
     * @param amount stack size
     * @return optional containing ItemStack if item found
     */
    Optional<ItemStack> createItemStack(String itemId, int amount);
    
    /**
     * Creates an ItemStack with amount 1.
     * 
     * @param itemId item identifier
     * @return optional containing ItemStack if item found
     */
    default Optional<ItemStack> createItemStack(String itemId) {
        return createItemStack(itemId, 1);
    }
    
    /**
     * Registers an item ability.
     * 
     * @param ability ability to register
     * @return future that completes when registered
     */
    CompletableFuture<Void> registerAbility(ItemAbility ability);
    
    /**
     * Gets an ability by ID.
     * 
     * @param abilityId ability identifier
     * @return optional containing ability if found
     */
    Optional<ItemAbility> getAbility(String abilityId);
    
    /**
     * Checks if a player can use an ability (cooldown check).
     * 
     * @param playerId player UUID
     * @param abilityId ability identifier
     * @return true if can use
     */
    boolean canUseAbility(java.util.UUID playerId, String abilityId);
    
    /**
     * Sets an ability on cooldown for a player.
     * 
     * @param playerId player UUID
     * @param abilityId ability identifier
     * @param cooldownTicks cooldown in ticks
     */
    void setCooldown(java.util.UUID playerId, String abilityId, long cooldownTicks);
    
    /**
     * Gets remaining cooldown for an ability.
     * 
     * @param playerId player UUID
     * @param abilityId ability identifier
     * @return remaining ticks, or 0 if no cooldown
     */
    long getRemainingCooldown(java.util.UUID playerId, String abilityId);
}
