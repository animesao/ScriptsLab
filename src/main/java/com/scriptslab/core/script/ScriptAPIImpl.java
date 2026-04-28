package com.scriptslab.core.script;

import com.scriptslab.ScriptsLabPlugin;
import com.scriptslab.api.event.EventBus;
import com.scriptslab.api.item.CustomItem;
import com.scriptslab.core.item.CustomItemImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * API exposed to JavaScript scripts.
 * Provides safe access to plugin functionality.
 */
public final class ScriptAPIImpl implements Listener {
    
    private final ScriptsLabPlugin plugin;
    private final LegacyComponentSerializer serializer;
    private java.util.Map eventExecutors;
    private java.util.Map handlersMap;
    private java.util.Map<String, Object> commandCodeMap;
    private java.util.Map<String, String> commandSourceMap;
    
    public ScriptAPIImpl(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
        this.serializer = LegacyComponentSerializer.legacyAmpersand();
        this.eventExecutors = new java.util.HashMap();
        this.handlersMap = new java.util.HashMap();
        this.commandCodeMap = new java.util.HashMap();
        this.commandSourceMap = new java.util.HashMap();
    }
    
    public void storeCommand(String name, Object executor) {
        commandCodeMap.put(name, executor);
    }
    
    public void storeCommandSource(String name, String jsCode) {
        commandSourceMap.put(name, jsCode);
    }
    
    public Object getCommand(String name) {
        return commandCodeMap.get(name);
    }
    
    public String getCommandSource(String name) {
        return commandSourceMap.get(name);
    }
    
    public java.util.function.BiConsumer<org.bukkit.command.CommandSender, String[]> createCommand(String jsCode) {
        return (sender, args) -> {
            String code = jsCode
                .replace("{sender}", "sender")
                .replace("{args}", "args");
            plugin.getScriptEngine().execute(code);
        };
    }
    
    // === Player Management ===
    
    public Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }
    
    public Player getPlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
    
    public List<Player> getOnlinePlayers() {
        return List.copyOf(Bukkit.getOnlinePlayers());
    }
    
    public void sendMessage(Player player, String message) {
        Component component = serializer.deserialize(message);
        player.sendMessage(component);
    }
    
    public void broadcast(String message) {
        Component component = serializer.deserialize(message);
        Bukkit.broadcast(component);
    }
    
    // === Item Management ===
    
    public void registerItem(String id, String materialName, String displayName, String... lore) {
        try {
            Material material = Material.valueOf(materialName.toUpperCase());
            
            CustomItem item = new CustomItemImpl.Builder(id)
                    .material(material)
                    .displayName(displayName)
                    .lore(Arrays.asList(lore))
                    .build();
            
            plugin.getItemManager().registerItem(item).join();
            
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material: " + materialName);
        }
    }
    
    public void giveItem(Player player, String itemId, int amount) {
        log("giveItem called: " + itemId + " to " + player.getName());
        java.util.Optional<ItemStack> result = plugin.getItemManager().createItemStack(itemId, amount);
        if (result.isPresent()) {
            player.getInventory().addItem(result.get());
            log("Item given successfully!");
        } else {
            log("Item NOT found: " + itemId);
        }
    }
    
    public void giveItem(Player player, String itemId) {
        giveItem(player, itemId, 1);
    }
    
    // === Event Management ===
    
    /**
     * Register event handler by event name (string).
     * Usage: Events.on('PlayerJoinEvent', function(event) { ... });
     */
    public void on(String eventName, Object handler) {
        try {
            // Try to find the event class
            Class<?> eventClass = null;
            
            // Common event packages
            String[] packages = {
                "org.bukkit.event.player.",
                "org.bukkit.event.entity.",
                "org.bukkit.event.block.",
                "org.bukkit.event.world.",
                "org.bukkit.event.server.",
                "org.bukkit.event.inventory."
            };
            
            for (String pkg : packages) {
                try {
                    eventClass = Class.forName(pkg + eventName);
                    break;
                } catch (ClassNotFoundException ignored) {
                }
            }
            
            if (eventClass == null) {
                log("Warning: Event class not found: " + eventName);
                return;
            }
            
            @SuppressWarnings("unchecked")
            Class<? extends Event> eventType = (Class<? extends Event>) eventClass;
            on(eventType, () -> {
                // Handler will be called - but we need to pass event to JS function
                // This is simplified - full implementation would pass event object
            });
            
        } catch (Exception e) {
            log("Error registering event " + eventName + ": " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void on(Class<? extends Event> eventClass, Runnable handler) {
        java.util.List<Runnable> handlers = (java.util.List<Runnable>) handlersMap.get(eventClass);
        if (handlers == null) {
            handlers = new java.util.ArrayList<>();
            handlersMap.put(eventClass, handlers);
        }
        handlers.add(handler);
        
        if (!eventExecutors.containsKey(eventClass)) {
            final java.util.List<Runnable> finalHandlers = handlers;
            org.bukkit.plugin.EventExecutor executor = (listener, event) -> {
                java.util.List<Runnable> eventHandlers = (java.util.List<Runnable>) handlersMap.get(event.getClass());
                if (eventHandlers != null) {
                    for (Runnable r : eventHandlers) {
                        try { r.run(); } catch (Exception e) { plugin.getLogger().warning("Event error: " + e.getMessage()); }
                    }
                }
            };
            
            plugin.getServer().getPluginManager().registerEvent(
                eventClass, this, org.bukkit.event.EventPriority.NORMAL, executor, plugin
            );
            eventExecutors.put(eventClass, executor);
            log("Registered event: " + eventClass.getSimpleName());
        }
    }
    private final ScriptAPIImpl scriptAPIImplListener = this;
    
    // === Scheduler ===
    // NOTE: Disabled due to GraalVM JS multithreading restrictions
    // JS callbacks cannot be invoked from Bukkit scheduler
    
    public void runLater(Runnable task, long ticks) {
        // Disabled - cannot schedule JS callbacks from different thread
        plugin.getLogger().warning("Scheduler.runLater() disabled - GraalVM JS restrictions");
    }
    
    public void runAsync(Runnable task) {
        // Disabled
        plugin.getLogger().warning("Scheduler.runAsync() disabled - GraalVM JS restrictions");
    }
    
    public int runTimer(Runnable task, long delayTicks, long periodTicks) {
        // Disabled
        plugin.getLogger().warning("Scheduler.runTimer() disabled - GraalVM JS restrictions");
        return -1;
    }
    
    // === Logging ===
    
    public void log(String message) {
        plugin.getLogger().info("[Script] " + message);
    }
    
    public void warn(String message) {
        plugin.getLogger().warning("[Script] " + message);
    }
    
    public void error(String message) {
        plugin.getLogger().severe("[Script] " + message);
    }
    
    // === Command Registration ===
    
    /**
     * Alias for registerCommand - for convenience in scripts.
     * Usage: Commands.register('mycommand', function(sender, args) { ... });
     */
    public void register(String name, Object executor) {
        registerCommand(name, executor, null);
    }
    
    /**
     * Alias for registerCommand with permission.
     * Usage: Commands.register('mycommand', function(sender, args) { ... }, 'permission');
     */
    public void register(String name, Object executor, String permission) {
        registerCommand(name, executor, permission);
    }
    
    public void registerCommand(String name, Object executor) {
        registerCommand(name, executor, null);
    }
    
    /**
     * Registers a command from script - accepts JavaScript function.
     */
    public void registerCommand(String name, Object executor, String permission) {
        try {
            final String cmdName = name;
            
            // Store the executor function for later invocation
            storeCommand(cmdName, executor);
            
            plugin.getCommandManager().registerScriptCommand(name, (sender, args) -> {
                try {
                    // Get the stored executor
                    Object storedExecutor = getCommand(cmdName);
                    if (storedExecutor == null) {
                        sender.sendMessage("§cCommand executor not found!");
                        return;
                    }
                    
                    // Check if sender is a player
                    org.bukkit.entity.Player player = null;
                    if (sender instanceof org.bukkit.entity.Player) {
                        player = (org.bukkit.entity.Player) sender;
                    }
                    
                    // Build JavaScript code to invoke the stored function
                    String argArray = makeArray(args);
                    
                    // Create sender wrapper object
                    String senderWrapper;
                    if (player != null) {
                        String playerName = player.getName();
                        senderWrapper = "{ " +
                            "isPlayer: function() { return true; }, " +
                            "getName: function() { return '" + playerName + "'; }, " +
                            "sendMessage: function(m) { var p = org.bukkit.Bukkit.getPlayer('" + playerName + "'); if(p) { p.sendMessage(m); } }, " +
                            "getInventory: function() { return org.bukkit.Bukkit.getPlayer('" + playerName + "').getInventory(); }, " +
                            "hasPermission: function(perm) { return org.bukkit.Bukkit.getPlayer('" + playerName + "').hasPermission(perm); }, " +
                            "getLocation: function() { return org.bukkit.Bukkit.getPlayer('" + playerName + "').getLocation(); }, " +
                            "getWorld: function() { return org.bukkit.Bukkit.getPlayer('" + playerName + "').getWorld(); }, " +
                            "setAllowFlight: function(v) { org.bukkit.Bukkit.getPlayer('" + playerName + "').setAllowFlight(v); }, " +
                            "getAllowFlight: function() { return org.bukkit.Bukkit.getPlayer('" + playerName + "').getAllowFlight(); }, " +
                            "setHealth: function(v) { org.bukkit.Bukkit.getPlayer('" + playerName + "').setHealth(v); }, " +
                            "getMaxHealth: function() { return org.bukkit.Bukkit.getPlayer('" + playerName + "').getMaxHealth(); }, " +
                            "setFoodLevel: function(v) { org.bukkit.Bukkit.getPlayer('" + playerName + "').setFoodLevel(v); }, " +
                            "setSaturation: function(v) { org.bukkit.Bukkit.getPlayer('" + playerName + "').setSaturation(v); }, " +
                            "getActivePotionEffects: function() { return org.bukkit.Bukkit.getPlayer('" + playerName + "').getActivePotionEffects(); }, " +
                            "removePotionEffect: function(t) { org.bukkit.Bukkit.getPlayer('" + playerName + "').removePotionEffect(t); } " +
                            "}";
                    } else {
                        senderWrapper = "{ " +
                            "isPlayer: function() { return false; }, " +
                            "getName: function() { return '" + sender.getName() + "'; }, " +
                            "sendMessage: function(m) { /* console sender */ } " +
                            "}";
                    }
                    
                    // Get the stored function from the command map and invoke it
                    String code = "(function() { " +
                            "var executor = API.getCommand('" + cmdName + "'); " +
                            "if (executor && typeof executor === 'function') { " +
                            "  var sender = " + senderWrapper + "; " +
                            "  var args = " + argArray + "; " +
                            "  executor(sender, args); " +
                            "} else { " +
                            "  Console.error('Executor is not a function for command: " + cmdName + "'); " +
                            "} " +
                            "})();";
                    
                    plugin.getScriptEngine().execute(code).exceptionally(ex -> {
                        sender.sendMessage("§cError: " + ex.getMessage());
                        log("Error /" + cmdName + ": " + ex.getMessage());
                        ex.printStackTrace();
                        return null;
                    });
                } catch (Exception e) {
                    sender.sendMessage("§cError: " + e.getMessage());
                    log("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }, permission).join();
            
            log("Registered command: /" + name);
        } catch (Exception e) {
            log("Failed to register command: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String makeArray(String[] arr) {
        if (arr == null || arr.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i].replace("\\", "\\\\").replace("'", "\\'");
            sb.append("'").append(s).append("'");
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    // === Inventory Management ===
    
    /**
     * Gives an item to a player.
     */
    public void giveItemStack(Player player, ItemStack item) {
        player.getInventory().addItem(item);
    }
    
    /**
     * Removes an item from player inventory.
     */
    public void removeItem(Player player, Material material, int amount) {
        player.getInventory().removeItem(new ItemStack(material, amount));
    }
    
    /**
     * Clears player inventory.
     */
    public void clearInventory(Player player) {
        player.getInventory().clear();
    }
    
    // === World Management ===
    
    /**
     * Gets a world by name.
     */
    public org.bukkit.World getWorld(String name) {
        return Bukkit.getWorld(name);
    }
    
    /**
     * Gets all worlds.
     */
    public List<org.bukkit.World> getWorlds() {
        return Bukkit.getWorlds();
    }
    
    // === Metrics ===
    
    /**
     * Records a metric.
     */
    public void recordMetric(String name, double value) {
        plugin.getMetricsCollector().record(name, value);
    }
    
    /**
     * Increments a counter.
     */
    public void incrementCounter(String name) {
        plugin.getMetricsCollector().increment(name);
    }
    
    // === Utility ===
    
    public org.bukkit.Server getServer() {
        return Bukkit.getServer();
    }
    
    public ScriptsLabPlugin getPlugin() {
        return plugin;
    }
    
    // === Config Access ===
    
    public boolean getConfigBoolean(String path, boolean defaultValue) {
        return plugin.getConfig().getBoolean(path, defaultValue);
    }
    
    public String getConfigString(String path, String defaultValue) {
        return plugin.getConfig().getString(path, defaultValue);
    }
    
    public int getConfigInt(String path, int defaultValue) {
        return plugin.getConfig().getInt(path, defaultValue);
    }
    
    // === Item Creation ===
    
    public void createItem(String id, String materialName, String displayName, String... lore) {
        registerItem(id, materialName, displayName, lore);
    }
    
    // === Attribute Helper ===
    
    /**
     * Add attribute modifier to item meta.
     * Usage: API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'my_modifier', 10.0, 'ADD_NUMBER', 'HAND');
     */
    public void addAttribute(org.bukkit.inventory.meta.ItemMeta meta, String attributeName, String modifierName, double value, String operation, String slot) {
        try {
            // Get attribute
            org.bukkit.attribute.Attribute attribute = org.bukkit.attribute.Attribute.valueOf(attributeName);
            
            // Get operation
            org.bukkit.attribute.AttributeModifier.Operation op = 
                org.bukkit.attribute.AttributeModifier.Operation.valueOf(operation);
            
            // Get slot
            org.bukkit.inventory.EquipmentSlot equipSlot = 
                org.bukkit.inventory.EquipmentSlot.valueOf(slot);
            
            // Create modifier
            org.bukkit.attribute.AttributeModifier modifier = new org.bukkit.attribute.AttributeModifier(
                java.util.UUID.randomUUID(),
                modifierName,
                value,
                op,
                equipSlot
            );
            
            // Add to meta
            meta.addAttributeModifier(attribute, modifier);
            
            log("Added attribute " + attributeName + " with value " + value);
            
        } catch (Exception e) {
            log("Failed to add attribute: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // === Event Registration Helper ===
    
    /**
     * Register event handler with JavaScript function.
     * Usage from JS: API.registerEvent('EntityDamageByEntityEvent', function(event) { ... });
     */
    public void registerEvent(String eventClassName, Object handler) {
        try {
            // Find event class
            Class<?> eventClass = null;
            
            // Try common packages
            String[] packages = {
                "org.bukkit.event.entity.",
                "org.bukkit.event.player.",
                "org.bukkit.event.block.",
                "org.bukkit.event.world.",
                "org.bukkit.event.server.",
                "org.bukkit.event.inventory."
            };
            
            for (String pkg : packages) {
                try {
                    eventClass = Class.forName(pkg + eventClassName);
                    break;
                } catch (ClassNotFoundException ignored) {
                }
            }
            
            if (eventClass == null) {
                log("Event class not found: " + eventClassName);
                return;
            }
            
            @SuppressWarnings("unchecked")
            Class<? extends Event> eventType = (Class<? extends Event>) eventClass;
            
            // Create event executor that calls the JS function IN SYNC CONTEXT
            org.bukkit.plugin.EventExecutor executor = (listener, event) -> {
                if (eventType.isInstance(event)) {
                    // Execute in main thread using scheduler
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        try {
                            // Call the JavaScript function with event as parameter
                            String code = "(function() { " +
                                    "var handler = API.getEventHandler('" + eventClassName + "'); " +
                                    "if (handler && typeof handler === 'function') { " +
                                    "  var event = API.getCurrentEvent(); " +
                                    "  handler(event); " +
                                    "} " +
                                    "})();";
                            
                            // Store event temporarily
                            storeCurrentEvent(event);
                            
                            plugin.getScriptEngine().execute(code).exceptionally(ex -> {
                                log("Error in event handler " + eventClassName + ": " + ex.getMessage());
                                return null;
                            }).join();
                            
                        } catch (Exception e) {
                            log("Error executing event handler: " + e.getMessage());
                        }
                    });
                }
            };
            
            // Store handler
            storeEventHandler(eventClassName, handler);
            
            // Register event
            plugin.getServer().getPluginManager().registerEvent(
                    eventType, 
                    this, 
                    org.bukkit.event.EventPriority.NORMAL, 
                    executor, 
                    plugin
            );
            
            log("Registered event handler: " + eventClassName);
            
        } catch (Exception e) {
            log("Failed to register event " + eventClassName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private java.util.Map<String, Object> eventHandlers = new java.util.HashMap<>();
    private Event currentEvent;
    
    public void storeEventHandler(String name, Object handler) {
        eventHandlers.put(name, handler);
    }
    
    public Object getEventHandler(String name) {
        return eventHandlers.get(name);
    }
    
    public void storeCurrentEvent(Event event) {
        this.currentEvent = event;
    }
    
    public Event getCurrentEvent() {
        return currentEvent;
    }
    
    // === Safe Bukkit API Wrappers (Thread-Safe) ===
    
    /**
     * Safely add potion effect to player (thread-safe).
     * Usage from JS: API.addPotionEffectSync(player, effectType, duration, amplifier, ambient, particles);
     */
    public void addPotionEffectSync(org.bukkit.entity.Player player, org.bukkit.potion.PotionEffectType effectType, int duration, int amplifier, boolean ambient, boolean particles) {
        if (plugin.getServer().isPrimaryThread()) {
            // Already on main thread
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(effectType, duration, amplifier, ambient, particles));
        } else {
            // Schedule on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(effectType, duration, amplifier, ambient, particles));
            });
        }
    }
    
    /**
     * Safely strike lightning (thread-safe).
     * Usage from JS: API.strikeLightningSync(location);
     */
    public void strikeLightningSync(org.bukkit.Location location) {
        if (plugin.getServer().isPrimaryThread()) {
            // Already on main thread
            location.getWorld().strikeLightning(location);
        } else {
            // Schedule on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                location.getWorld().strikeLightning(location);
            });
        }
    }
    
    /**
     * Safely remove potion effect (thread-safe).
     * Usage from JS: API.removePotionEffectSync(player, effectType);
     */
    public void removePotionEffectSync(org.bukkit.entity.Player player, org.bukkit.potion.PotionEffectType effectType) {
        if (plugin.getServer().isPrimaryThread()) {
            // Already on main thread
            player.removePotionEffect(effectType);
        } else {
            // Schedule on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.removePotionEffect(effectType);
            });
        }
    }
}
