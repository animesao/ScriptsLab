package com.scriptslab.core.config;

import com.scriptslab.ScriptsLabPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Centralized configuration manager.
 * Handles main config, messages, and module configs.
 */
public final class ConfigurationManager {
    
    private final ScriptsLabPlugin plugin;
    private final Map<String, FileConfiguration> configs;
    private final LegacyComponentSerializer serializer;
    
    private FileConfiguration messagesConfig;
    
    public ConfigurationManager(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
        this.configs = new HashMap<>();
        this.serializer = LegacyComponentSerializer.legacyAmpersand();
        
        // Load messages
        loadMessages();
    }
    
    /**
     * Loads the messages configuration.
     */
    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    /**
     * Reloads all configurations.
     */
    public void reload() {
        plugin.reloadConfig();
        configs.clear();
        loadMessages();
        plugin.getLogger().info("Configurations reloaded");
    }
    
    /**
     * Gets a configuration by name.
     * 
     * @param name config name (without .yml)
     * @return configuration
     */
    public FileConfiguration getConfig(String name) {
        if (name.equals("config")) {
            return plugin.getConfig();
        }
        
        if (name.equals("messages")) {
            return messagesConfig;
        }
        
        return configs.computeIfAbsent(name, this::loadConfig);
    }
    
    /**
     * Loads a configuration file.
     */
    private FileConfiguration loadConfig(String name) {
        File configFile = new File(plugin.getDataFolder(), name + ".yml");
        
        if (!configFile.exists()) {
            plugin.getLogger().warning("Config file not found: " + name + ".yml");
            return new YamlConfiguration();
        }
        
        return YamlConfiguration.loadConfiguration(configFile);
    }
    
    /**
     * Saves a configuration.
     * 
     * @param name config name
     * @param config configuration to save
     */
    public void saveConfig(String name, FileConfiguration config) {
        try {
            File configFile = new File(plugin.getDataFolder(), name + ".yml");
            config.save(configFile);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config: " + name, e);
        }
    }
    
    // === Message Methods ===
    
    /**
     * Gets a message from messages.yml.
     * 
     * @param path message path
     * @return message string
     */
    public String getMessage(String path) {
        return messagesConfig.getString(path, path);
    }
    
    /**
     * Gets a message with placeholders replaced.
     * 
     * @param path message path
     * @param placeholders placeholder map
     * @return formatted message
     */
    public String getMessage(String path, Map<String, String> placeholders) {
        String message = getMessage(path);
        
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        
        return message;
    }
    
    /**
     * Gets a message as a Component.
     * 
     * @param path message path
     * @return component
     */
    public Component getMessageComponent(String path) {
        return serializer.deserialize(getMessage(path));
    }
    
    /**
     * Gets a message as a Component with placeholders.
     * 
     * @param path message path
     * @param placeholders placeholder map
     * @return component
     */
    public Component getMessageComponent(String path, Map<String, String> placeholders) {
        return serializer.deserialize(getMessage(path, placeholders));
    }
    
    /**
     * Creates a placeholder map.
     * 
     * @param pairs key-value pairs
     * @return placeholder map
     */
    public static Map<String, String> placeholders(String... pairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < pairs.length - 1; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }
}
