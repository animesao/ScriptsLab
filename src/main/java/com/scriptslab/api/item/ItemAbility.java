package com.scriptslab.api.item;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an ability that can be attached to a custom item.
 */
public interface ItemAbility {
    
    /**
     * Gets the unique identifier of this ability.
     * 
     * @return ability ID
     */
    String getId();
    
    /**
     * Gets the display name of this ability.
     * 
     * @return ability name
     */
    String getName();
    
    /**
     * Gets the description of this ability.
     * 
     * @return description
     */
    String getDescription();
    
    /**
     * Gets the cooldown in ticks.
     * 
     * @return cooldown ticks
     */
    long getCooldown();
    
    /**
     * Gets the trigger type for this ability.
     * 
     * @return trigger type
     */
    TriggerType getTrigger();
    
    /**
     * Executes the ability.
     * 
     * @param player player using the ability
     * @param event triggering event
     * @return future that completes when execution is done
     */
    CompletableFuture<Boolean> execute(Player player, Event event);
    
    /**
     * Checks if the ability can be used.
     * 
     * @param player player attempting to use
     * @return true if can be used
     */
    boolean canUse(Player player);
    
    /**
     * Trigger types for abilities.
     */
    enum TriggerType {
        RIGHT_CLICK,
        LEFT_CLICK,
        SHIFT_RIGHT_CLICK,
        SHIFT_LEFT_CLICK,
        HIT_ENTITY,
        DAMAGE_ENTITY,
        TAKE_DAMAGE,
        BREAK_BLOCK,
        PLACE_BLOCK,
        EQUIP,
        UNEQUIP,
        PASSIVE
    }
}
