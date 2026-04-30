package com.scriptslab.core.item;

import com.scriptslab.api.item.CustomItem;
import com.scriptslab.api.item.ItemAbility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Immutable implementation of CustomItem.
 */
public record CustomItemImpl(
        String id,
        Material material,
        String displayName,
        List<String> lore,
        int customModelData,
        Map<String, Object> nbtData,
        List<ItemAbility> abilities,
        boolean unbreakable,
        Rarity rarity
) implements CustomItem {
    
    private static final LegacyComponentSerializer SERIALIZER = 
            LegacyComponentSerializer.legacyAmpersand();
    
    /**
     * Compact constructor with validation and defensive copying.
     */
    public CustomItemImpl {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item ID cannot be null or blank");
        }
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        if (displayName == null) {
            displayName = id;
        }
        
        // Defensive copies
        lore = lore != null ? List.copyOf(lore) : List.of();
        nbtData = nbtData != null ? Map.copyOf(nbtData) : Map.of();
        abilities = abilities != null ? List.copyOf(abilities) : List.of();
        
        if (rarity == null) {
            rarity = Rarity.COMMON;
        }
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public Material getMaterial() {
        return material;
    }
    
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public List<String> getLore() {
        return lore;
    }
    
    @Override
    public int getCustomModelData() {
        return customModelData;
    }
    
    @Override
    public Map<String, Object> getNbtData() {
        return nbtData;
    }
    
    @Override
    public List<ItemAbility> getAbilities() {
        return abilities;
    }
    
    @Override
    public boolean isUnbreakable() {
        return unbreakable;
    }
    
    @Override
    public Rarity getRarity() {
        return rarity;
    }
    
    @Override
    public ItemStack toItemStack(int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Display name with rarity color
            Component nameComponent = SERIALIZER.deserialize(rarity.getColor() + displayName);
            meta.displayName(nameComponent);
            
            // Lore
            if (!lore.isEmpty()) {
                List<Component> loreComponents = new ArrayList<>();
                for (String line : lore) {
                    loreComponents.add(SERIALIZER.deserialize(line));
                }
                
                // Add rarity to lore
                loreComponents.add(Component.empty());
                loreComponents.add(SERIALIZER.deserialize(rarity.getColor() + rarity.getDisplayName()));
                
                meta.lore(loreComponents);
            }
            
            // Custom model data
            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }
            
            // Unbreakable
            if (unbreakable) {
                meta.setUnbreakable(true);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Builder for creating CustomItem instances.
     */
    public static class Builder {
        private String id;
        private Material material = Material.STONE;
        private String displayName;
        private List<String> lore = new ArrayList<>();
        private int customModelData = 0;
        private Map<String, Object> nbtData = new java.util.HashMap<>();
        private List<ItemAbility> abilities = new ArrayList<>();
        private boolean unbreakable = false;
        private Rarity rarity = Rarity.COMMON;
        
        public Builder(String id) {
            this.id = id;
            this.displayName = id;
        }
        
        public Builder material(Material material) {
            this.material = material;
            return this;
        }
        
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }
        
        public Builder lore(String... lore) {
            this.lore = new ArrayList<>(List.of(lore));
            return this;
        }
        
        public Builder lore(List<String> lore) {
            this.lore = new ArrayList<>(lore);
            return this;
        }
        
        public Builder addLore(String line) {
            this.lore.add(line);
            return this;
        }
        
        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }
        
        public Builder nbt(String key, Object value) {
            this.nbtData.put(key, value);
            return this;
        }
        
        public Builder nbtData(Map<String, Object> nbtData) {
            this.nbtData = new java.util.HashMap<>(nbtData);
            return this;
        }
        
        public Builder ability(ItemAbility ability) {
            this.abilities.add(ability);
            return this;
        }
        
        public Builder abilities(List<ItemAbility> abilities) {
            this.abilities = new ArrayList<>(abilities);
            return this;
        }
        
        public Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }
        
        public Builder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }
        
        public CustomItem build() {
            return new CustomItemImpl(
                    id, material, displayName, lore, customModelData,
                    nbtData, abilities, unbreakable, rarity
            );
        }
    }
}
