package com.scriptslab.api.command;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Manager for plugin commands.
 * Supports both Java and script-based commands.
 */
public interface CommandManager {
    
    /**
     * Registers all plugin commands.
     */
    void registerCommands();
    
    /**
     * Registers a command from a script.
     * 
     * @param name command name
     * @param executor command executor
     * @param permission required permission (null for none)
     * @return future that completes when registered
     */
    CompletableFuture<Void> registerScriptCommand(
            String name,
            BiConsumer<CommandSender, String[]> executor,
            String permission
    );
    
    /**
     * Registers a command with tab completion.
     * 
     * @param name command name
     * @param executor command executor
     * @param tabCompleter tab completer
     * @param permission required permission
     * @return future that completes when registered
     */
    CompletableFuture<Void> registerScriptCommand(
            String name,
            BiConsumer<CommandSender, String[]> executor,
            BiFunction<CommandSender, String[], List<String>> tabCompleter,
            String permission
    );
    
    /**
     * Unregisters a command.
     * 
     * @param name command name
     * @return future that completes when unregistered
     */
    CompletableFuture<Void> unregisterCommand(String name);
    
    /**
     * Checks if a command is registered.
     * 
     * @param name command name
     * @return true if registered
     */
    boolean isRegistered(String name);
    
    /**
     * Gets all registered command names.
     * 
     * @return list of command names
     */
    List<String> getRegisteredCommands();
}
