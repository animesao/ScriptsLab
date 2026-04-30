package com.scriptslab.core.script;

import com.scriptslab.ScriptsLabPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.graalvm.polyglot.Value;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * GUI / Inventory API for JavaScript scripts.
 * Allows creating interactive chest menus.
 *
 * Usage in JS:
 *   var gui = GUI.create('My Menu', 27);
 *   gui.setItem(0, 'DIAMOND', '&bClick me!', function(player) {
 *       player.sendMessage('Clicked!');
 *   });
 *   gui.open(player);
 *   gui.onClose(function(player) { Console.log(player.getName() + ' closed'); });
 */
public final class ScriptGUI implements Listener {

    private final ScriptsLabPlugin plugin;
    private final Logger logger;
    private final LegacyComponentSerializer serializer;

    // Maps open inventory -> GUI instance (for click/close handling)
    private final Map<UUID, GUIInstance> openGUIs;

    public ScriptGUI(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
        this.logger = Logger.getLogger("ScriptGUI");
        this.serializer = LegacyComponentSerializer.legacyAmpersand();
        this.openGUIs = new ConcurrentHashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Creates a new GUI with the given title and size.
     *
     * @param title  display title (supports & color codes)
     * @param size   inventory size (must be multiple of 9, max 54)
     * @return GUIInstance builder
     */
    public GUIInstance create(String title, int size) {
        int clampedSize = Math.max(9, Math.min(54, (size / 9) * 9));
        return new GUIInstance(title, clampedSize);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        GUIInstance gui = openGUIs.get(player.getUniqueId());
        if (gui == null) return;

        // Only handle clicks in the top inventory (the GUI itself)
        if (!event.getInventory().equals(event.getClickedInventory())) return;

        event.setCancelled(true);

        int slot = event.getSlot();
        Value handler = gui.clickHandlers.get(slot);
        if (handler != null && handler.canExecute()) {
            try {
                handler.execute(player);
            } catch (Exception e) {
                logger.warning("[ScriptGUI] Click handler error at slot " + slot + ": " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        GUIInstance gui = openGUIs.remove(player.getUniqueId());
        if (gui == null) return;

        if (gui.closeHandler != null && gui.closeHandler.canExecute()) {
            // Schedule to next tick to avoid issues with close event
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                try {
                    gui.closeHandler.execute(player);
                } catch (Exception e) {
                    logger.warning("[ScriptGUI] Close handler error: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Represents a GUI menu instance.
     */
    public final class GUIInstance {

        private final String title;
        private final int size;
        private final Inventory inventory;
        private final Map<Integer, Value> clickHandlers;
        private Value closeHandler;

        GUIInstance(String title, int size) {
            this.title = title;
            this.size = size;
            this.clickHandlers = new HashMap<>();
            Component titleComponent = serializer.deserialize(title);
            this.inventory = Bukkit.createInventory(null, size, titleComponent);
        }

        /**
         * Sets an item in a slot with a click handler.
         *
         * @param slot     slot index (0-based)
         * @param material material name (e.g. "DIAMOND")
         * @param name     display name (supports & color codes)
         * @param handler  JS function(player) called on click (can be null)
         * @param lore     optional lore lines
         */
        public GUIInstance setItem(int slot, String material, String name, Object handler, String... lore) {
            if (slot < 0 || slot >= size) return this;

            try {
                Material mat = Material.valueOf(material.toUpperCase());
                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.displayName(serializer.deserialize(name));
                    if (lore.length > 0) {
                        List<Component> loreComponents = new ArrayList<>();
                        for (String line : lore) {
                            loreComponents.add(serializer.deserialize(line));
                        }
                        meta.lore(loreComponents);
                    }
                    item.setItemMeta(meta);
                }
                inventory.setItem(slot, item);

                if (handler instanceof Value v && v.canExecute()) {
                    clickHandlers.put(slot, v);
                }
            } catch (IllegalArgumentException e) {
                logger.warning("[ScriptGUI] Invalid material: " + material);
            }
            return this;
        }

        /**
         * Sets an item without a click handler (decorative).
         */
        public GUIInstance setItem(int slot, String material, String name, String... lore) {
            return setItem(slot, material, name, null, lore);
        }

        /**
         * Fills empty slots with a filler item (e.g. gray glass pane).
         */
        public GUIInstance fill(String material, String name) {
            for (int i = 0; i < size; i++) {
                if (inventory.getItem(i) == null) {
                    setItem(i, material, name);
                }
            }
            return this;
        }

        /**
         * Sets a close handler.
         *
         * @param handler JS function(player) called when GUI is closed
         */
        public GUIInstance onClose(Object handler) {
            if (handler instanceof Value v && v.canExecute()) {
                this.closeHandler = v;
            }
            return this;
        }

        /**
         * Clears a slot.
         */
        public GUIInstance clearSlot(int slot) {
            inventory.setItem(slot, null);
            clickHandlers.remove(slot);
            return this;
        }

        /**
         * Opens this GUI for a player.
         */
        public void open(Player player) {
            openGUIs.put(player.getUniqueId(), this);
            player.openInventory(inventory);
        }

        /**
         * Closes this GUI for a player.
         */
        public void close(Player player) {
            player.closeInventory();
        }

        /**
         * Updates an item in a slot without changing the handler.
         */
        public GUIInstance updateItem(int slot, String material, String name, String... lore) {
            return setItem(slot, material, name, clickHandlers.get(slot), lore);
        }

        public int getSize() {
            return size;
        }

        public String getTitle() {
            return title;
        }
    }
}
