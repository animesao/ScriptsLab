package com.scriptslab.core.command;

import com.scriptslab.ScriptsLabPlugin;
import com.scriptslab.api.command.CommandManager;
import com.scriptslab.core.command.commands.MainCommand;
import com.scriptslab.core.command.commands.ModuleCommand;
import com.scriptslab.core.command.commands.ScriptCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Level;

/**
 * Implementation of CommandManager.
 * Handles both plugin and script commands.
 */
public final class CommandManagerImpl implements CommandManager {
    
    private final ScriptsLabPlugin plugin;
    private final Map<String, ScriptCommandWrapper> scriptCommands;
    private final ReentrantLock commandLock;
    private CommandMap commandMap;
    
    public CommandManagerImpl(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
        this.scriptCommands = new ConcurrentHashMap<>();
        this.commandLock = new ReentrantLock();
        
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get CommandMap", e);
        }
    }
    
    @Override
    public void registerCommands() {
        commandLock.lock();
        try {
            registerPluginCommand("scriptslab", new MainCommand(plugin));
            registerPluginCommand("module", new ModuleCommand(plugin));
            registerPluginCommand("script", new ScriptCommand(plugin));
            
            plugin.getLogger().info("Plugin commands registered");
        } finally {
            commandLock.unlock();
        }
    }
    
    /**
     * Registers a plugin command.
     */
    private void registerPluginCommand(String name, org.bukkit.command.CommandExecutor executor) {
        PluginCommand command = plugin.getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
            
            if (executor instanceof org.bukkit.command.TabCompleter) {
                command.setTabCompleter((org.bukkit.command.TabCompleter) executor);
            }
        }
    }
    
    @Override
    public CompletableFuture<Void> registerScriptCommand(
            String name,
            BiConsumer<CommandSender, String[]> executor,
            String permission) {
        
        return registerScriptCommand(name, executor, null, permission);
    }
    
    @Override
    public CompletableFuture<Void> registerScriptCommand(
            String name,
            BiConsumer<CommandSender, String[]> executor,
            BiFunction<CommandSender, String[], List<String>> tabCompleter,
            String permission) {
        
        return CompletableFuture.runAsync(() -> {
            commandLock.lock();
            try {
                if (commandMap == null) {
                    plugin.getLogger().warning("CommandMap not available, cannot register script command: " + name);
                    return;
                }
                
                ScriptCommandWrapper wrapper = new ScriptCommandWrapper(
                        name, executor, tabCompleter, permission);
                
                commandMap.register("scriptslab", wrapper);
                scriptCommands.put(name, wrapper);
                
                plugin.getLogger().fine("Registered script command: " + name);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error registering command: " + name, e);
            } finally {
                commandLock.unlock();
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> unregisterCommand(String name) {
        return CompletableFuture.runAsync(() -> {
            commandLock.lock();
            try {
                ScriptCommandWrapper wrapper = scriptCommands.remove(name);
                
                if (wrapper != null && commandMap != null) {
                    wrapper.unregister(commandMap);
                    plugin.getLogger().fine("Unregistered script command: " + name);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error unregistering command: " + name, e);
            } finally {
                commandLock.unlock();
            }
        });
    }
    
    @Override
    public boolean isRegistered(String name) {
        return scriptCommands.containsKey(name);
    }
    
    @Override
    public List<String> getRegisteredCommands() {
        return new ArrayList<>(scriptCommands.keySet());
    }
    
    /**
     * Wrapper for script commands.
     */
    private static class ScriptCommandWrapper extends Command {
        
        private final BiConsumer<CommandSender, String[]> executor;
        private final BiFunction<CommandSender, String[], List<String>> tabCompleter;
        
        ScriptCommandWrapper(
                String name,
                BiConsumer<CommandSender, String[]> executor,
                BiFunction<CommandSender, String[], List<String>> tabCompleter,
                String permission) {
            
            super(name);
            this.executor = executor;
            this.tabCompleter = tabCompleter;
            
            if (permission != null && !permission.isEmpty()) {
                setPermission(permission);
            }
        }
        
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
            try {
                executor.accept(sender, args);
                return true;
            } catch (Exception e) {
                sender.sendMessage("§cError executing command: " + e.getMessage());
                return false;
            }
        }
        
        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
            if (tabCompleter != null) {
                try {
                    return tabCompleter.apply(sender, args);
                } catch (Exception e) {
                    return List.of();
                }
            }
            return List.of();
        }
    }
}
