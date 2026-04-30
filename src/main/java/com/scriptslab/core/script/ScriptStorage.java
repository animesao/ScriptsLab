package com.scriptslab.core.script;

import com.scriptslab.ScriptsLabPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Simple persistent key-value storage for JavaScript scripts.
 * Each namespace maps to a separate YAML file.
 *
 * Usage in JS:
 *   var db = Storage.open('mydata');
 *   db.set('key', 'value');
 *   var val = db.get('key');
 *   db.has('key');
 *   db.remove('key');
 *   db.save();
 *   db.keys();
 */
public final class ScriptStorage {

    private final File storageDir;
    private final Map<String, Namespace> namespaces;

    public ScriptStorage(ScriptsLabPlugin plugin) {
        this.storageDir = new File(plugin.getDataFolder(), "script-data");
        this.namespaces = new ConcurrentHashMap<>();
        storageDir.mkdirs();
    }

    /**
     * Opens (or creates) a storage namespace.
     * Returns a Namespace object with get/set/save/etc.
     */
    public Namespace open(String name) {
        return namespaces.computeIfAbsent(name, n -> new Namespace(n, new File(storageDir, n + ".yml")));
    }

    /**
     * Saves all open namespaces to disk.
     */
    public void saveAll() {
        for (Namespace ns : namespaces.values()) {
            ns.save();
        }
    }

    /**
     * A single storage namespace backed by a YAML file.
     */
    public static final class Namespace {

        private final String name;
        private final File file;
        private final YamlConfiguration yaml;
        private final Map<String, Object> cache;

        Namespace(String name, File file) {
            this.name = name;
            this.file = file;
            this.cache = new ConcurrentHashMap<>();

            if (file.exists()) {
                this.yaml = YamlConfiguration.loadConfiguration(file);
                // Populate cache from file
                for (String key : yaml.getKeys(true)) {
                    if (!yaml.isConfigurationSection(key)) {
                        cache.put(key, yaml.get(key));
                    }
                }
            } else {
                this.yaml = new YamlConfiguration();
            }
        }

        /** Sets a value. Supports dot-notation paths (e.g. "player.score"). */
        public void set(String key, Object value) {
            cache.put(key, value);
            yaml.set(key, value);
        }

        /** Gets a value, or null if not found. */
        public Object get(String key) {
            if (cache.containsKey(key)) {
                return cache.get(key);
            }
            return yaml.get(key);
        }

        /** Gets a value with a default fallback. */
        public Object getOrDefault(String key, Object defaultValue) {
            Object val = get(key);
            return val != null ? val : defaultValue;
        }

        /** Returns true if the key exists. */
        public boolean has(String key) {
            return cache.containsKey(key) || yaml.contains(key);
        }

        /** Removes a key. */
        public void remove(String key) {
            cache.remove(key);
            yaml.set(key, null);
        }

        /** Returns all top-level keys. */
        public Set<String> keys() {
            return yaml.getKeys(false);
        }

        /** Returns all keys (deep). */
        public Set<String> allKeys() {
            return yaml.getKeys(true);
        }

        /** Increments a numeric value by 1 (creates with 0 if missing). */
        public double increment(String key) {
            return increment(key, 1);
        }

        /** Increments a numeric value by amount. */
        public double increment(String key, double amount) {
            Object current = get(key);
            double newVal = (current instanceof Number n ? n.doubleValue() : 0) + amount;
            set(key, newVal);
            return newVal;
        }

        /** Saves this namespace to disk. */
        public void save() {
            try {
                yaml.save(file);
            } catch (IOException e) {
                Logger.getLogger("ScriptStorage").warning(
                        "[ScriptStorage] Failed to save namespace '" + name + "': " + e.getMessage());
            }
        }

        /** Clears all data in this namespace (in-memory and on disk). */
        public void clear() {
            cache.clear();
            for (String key : yaml.getKeys(false)) {
                yaml.set(key, null);
            }
            save();
        }

        public String getName() {
            return name;
        }

        /** Returns a snapshot of all data as a plain map (for JS iteration). */
        public Map<String, Object> toMap() {
            Map<String, Object> result = new HashMap<>();
            for (String key : yaml.getKeys(true)) {
                if (!yaml.isConfigurationSection(key)) {
                    result.put(key, yaml.get(key));
                }
            }
            return result;
        }
    }
}
