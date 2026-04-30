package com.scriptslab.modules.demo;

import com.scriptslab.ScriptsLabPlugin;
import com.scriptslab.api.item.CustomItem;
import com.scriptslab.core.item.CustomItemImpl;
import com.scriptslab.api.module.ModuleDescriptor;
import com.scriptslab.core.module.BaseModule;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Demo module showcasing framework capabilities:
 * - Magic Wand item (teleport forward)
 * - /fly command (toggle flight)
 * - Welcome message on join
 */
public final class DemoModule extends BaseModule implements Listener {
    
    private static final String MAGIC_WAND_ID = "magic_wand";
    private final ScriptsLabPlugin plugin;
    private final LegacyComponentSerializer serializer;
    
    public DemoModule(ModuleDescriptor descriptor, Path dataFolder) {
        super(descriptor, dataFolder);
        this.plugin = ScriptsLabPlugin.getInstance();
        this.serializer = LegacyComponentSerializer.legacyAmpersand();
    }
    
    @Override
    public CompletableFuture<Void> onLoad() {
        return super.onLoad().thenRun(() -> {
            log("Demo module loading...");
            
            // Set default config values
            if (getConfig().get("welcome-message") == null) {
                getConfig().set("welcome-message", "&6Welcome to the server, {player}!");
                getConfig().set("magic-wand.enabled", true);
                getConfig().set("magic-wand.teleport-distance", 5);
                getConfig().set("magic-wand.cooldown", 20);
                saveConfig();
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> onEnable() {
        return super.onEnable().thenRun(() -> {
            log("Demo module enabling...");
            
            // Register event listeners
            Bukkit.getPluginManager().registerEvents(this, plugin);
            
            // Register magic wand item
            if (getConfig().getBoolean("magic-wand.enabled", true)) {
                registerMagicWand();
            }
            
            // Register /fly command
            registerFlyCommand();
            
            log("Demo module enabled successfully!");
        });
    }
    
    @Override
    public CompletableFuture<Void> onDisable() {
        return super.onDisable().thenRun(() -> {
            log("Demo module disabling...");
            
            // Unregister magic wand
            plugin.getItemManager().unregisterItem(MAGIC_WAND_ID).join();
            
            log("Demo module disabled");
        });
    }
    
    /**
     * Registers the magic wand item.
     */
    private void registerMagicWand() {
        int distance = getConfig().getInt("magic-wand.teleport-distance", 5);
        
        CustomItem magicWand = new CustomItemImpl.Builder(MAGIC_WAND_ID)
                .material(Material.STICK)
                .displayName("&6&lMagic Wand")
                .lore(
                        "&7Right-click to teleport forward",
                        "&7Distance: &e" + distance + " blocks",
                        "",
                        "&6Legendary Item"
                )
                .customModelData(1)
                .rarity(CustomItem.Rarity.LEGENDARY)
                .unbreakable(true)
                .build();
        
        plugin.getItemManager().registerItem(magicWand).join();
        log("Registered Magic Wand item");
    }
    
    /**
     * Registers the /fly command.
     */
    private void registerFlyCommand() {
        var command = plugin.getCommand("fly");
        if (command != null) {
            command.setExecutor((sender, cmd, label, args) -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cOnly players can use this command!");
                    return true;
                }
                
                boolean canFly = !player.getAllowFlight();
                player.setAllowFlight(canFly);
                player.setFlying(canFly);
                
                if (canFly) {
                    player.sendMessage(serializer.deserialize("&aFlight enabled!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                } else {
                    player.sendMessage(serializer.deserialize("&cFlight disabled!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
                }
                
                return true;
            });
            
            log("Registered /fly command");
        }
    }
    
    /**
     * Handles player join event.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Send welcome message
        String message = getConfig().getString("welcome-message", "&6Welcome, {player}!");
        message = message.replace("{player}", player.getName());
        
        Component component = serializer.deserialize(message);
        player.sendMessage(component);
        
        // Give magic wand to new players
        if (!player.hasPlayedBefore() && getConfig().getBoolean("magic-wand.enabled", true)) {
            plugin.getItemManager().createItemStack(MAGIC_WAND_ID)
                    .ifPresent(item -> {
                        player.getInventory().addItem(item);
                        player.sendMessage(serializer.deserialize("&aYou received a &6Magic Wand&a!"));
                    });
        }
    }
    
    /**
     * Handles magic wand usage.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && 
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Check if it's a magic wand
        if (!plugin.getItemManager().isCustomItem(item)) {
            return;
        }
        
        String itemId = plugin.getItemManager().getCustomItemId(item).orElse(null);
        if (!MAGIC_WAND_ID.equals(itemId)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Check cooldown
        if (!plugin.getItemManager().canUseAbility(player.getUniqueId(), MAGIC_WAND_ID)) {
            long remaining = plugin.getItemManager().getRemainingCooldown(
                    player.getUniqueId(), MAGIC_WAND_ID);
            player.sendMessage(serializer.deserialize(
                    "&cCooldown: &e" + (remaining / 20) + "s"));
            return;
        }
        
        // Teleport forward
        int distance = getConfig().getInt("magic-wand.teleport-distance", 5);
        teleportForward(player, distance);
        
        // Set cooldown
        int cooldown = getConfig().getInt("magic-wand.cooldown", 20);
        plugin.getItemManager().setCooldown(player.getUniqueId(), MAGIC_WAND_ID, cooldown);
    }
    
    /**
     * Teleports player forward.
     */
    private void teleportForward(Player player, int distance) {
        Location currentLoc = player.getLocation();
        Vector direction = currentLoc.getDirection().normalize();
        
        // Calculate new position
        Location newLoc = currentLoc.clone().add(direction.multiply(distance));
        
        // Find safe ground
        while (newLoc.getBlock().getType().isAir() && 
               newLoc.getY() > currentLoc.getWorld().getMinHeight()) {
            newLoc.subtract(0, 1, 0);
        }
        
        newLoc.add(0, 1, 0);
        newLoc.setDirection(direction);
        
        // Teleport
        player.teleport(newLoc);
        player.sendMessage(serializer.deserialize("&aTeleported!"));
        
        // Effects
        player.getWorld().spawnParticle(
                Particle.PORTAL, currentLoc, 50, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().spawnParticle(
                Particle.PORTAL, newLoc, 50, 0.5, 0.5, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }
}
