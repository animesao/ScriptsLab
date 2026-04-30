package com.scriptslab.api.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Represents a custom item with unique properties and abilities.
 * Immutable.
 */
public interface CustomItem {
    
    /**
     * Gets the unique identifier of this item.
     * 
     * @return item ID
     */
    String getId();
    
    /**
     * Gets the base material of this item.
     * 
     * @return material
     */
    Material getMaterial();
    
    /**
     * Gets the display name of this item.
     * 
     * @return display name with color codes
     */
    String getDisplayName();
    
    /**
     * Gets the lore (description) of this item.
     * 
     * @return list of lore lines
     */
    List<String> getLore();
    
    /**
     * Gets the custom model data for resource packs.
     * 
     * @return custom model data, or 0 if none
     */
    int getCustomModelData();
    
    /**
     * Gets custom NBT data for this item.
     * 
     * @return map of NBT key-value pairs
     */
    Map<String, Object> getNbtData();
    
    /**
     * Gets the abilities attached to this item.
     * 
     * @return list of abilities
     */
    List<ItemAbility> getAbilities();
    
    /**
     * Checks if this item is unbreakable.
     * 
     * @return true if unbreakable
     */
    boolean isUnbreakable();
    
    /**
     * Gets the rarity of this item.
     * 
     * @return rarity level
     */
    Rarity getRarity();
    
    /**
     * Creates an ItemStack from this custom item.
     * 
     * @param amount stack size
     * @return ItemStack instance
     */
    ItemStack toItemStack(int amount);
    
    /**
     * Creates an ItemStack with amount 1.
     * 
     * @return ItemStack instance
     */
    default ItemStack toItemStack() {
        return toItemStack(1);
    }
    
    /**
     * Item rarity levels.
     */
    enum Rarity {
        COMMON("§f", "Обычный"),
        UNCOMMON("§a", "Необычный"),
        RARE("§9", "Редкий"),
        EPIC("§5", "Эпический"),
        LEGENDARY("§6", "Легендарный"),
        MYTHIC("§c", "Мифический");
        
        private final String color;
        private final String displayName;
        
        Rarity(String color, String displayName) {
            this.color = color;
            this.displayName = displayName;
        }
        
        public String getColor() {
            return color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
