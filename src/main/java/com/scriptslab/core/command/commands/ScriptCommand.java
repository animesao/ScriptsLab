package com.scriptslab.core.command.commands;

import com.scriptslab.ScriptsLabPlugin;
import com.scriptslab.api.script.ScriptEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Script management command.
 * /script <list|reload|load|unload> [name]
 */
public class ScriptCommand implements CommandExecutor, TabCompleter {
    
    private final ScriptsLabPlugin plugin;
    
    public ScriptCommand(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("scriptslab.script")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "list" -> handleList(sender);
            case "reload" -> handleReload(sender, args);
            case "info" -> handleInfo(sender, args);
            default -> sendHelp(sender);
        }
        
        return true;
    }
    
    private void handleList(CommandSender sender) {
        var scripts = plugin.getScriptEngine().getAllScripts();
        
        sender.sendMessage("§6╔═══════════════════════════════════════╗");
        sender.sendMessage("§6║           Loaded Scripts              ║");
        sender.sendMessage("§6╚═══════════════════════════════════════╝");
        
        if (scripts.isEmpty()) {
            sender.sendMessage("§7No scripts loaded");
            return;
        }
        
        for (ScriptEngine.LoadedScript script : scripts) {
            String status = script.hasErrors() ? "§c✗ Error" : "§a✓ OK";
            sender.sendMessage(String.format("§e%s %s §7(executed %d times)", 
                    script.getId(), status, script.getExecutionCount()));
        }
        
        sender.sendMessage("");
        sender.sendMessage("§7Total: §e" + scripts.size() + " §7scripts");
    }
    
    private void handleReload(CommandSender sender, String[] args) {
        if (args.length < 2) {
            // Reload all scripts
            sender.sendMessage("§6Reloading all scripts...");
            
            plugin.getScriptEngine().reloadAllScripts()
                    .thenAccept(count -> sender.sendMessage("§aReloaded " + count + " scripts"))
                    .exceptionally(ex -> {
                        sender.sendMessage("§cFailed to reload scripts: " + ex.getMessage());
                        return null;
                    });
        } else {
            // Reload specific script
            String scriptName = args[1];
            sender.sendMessage("§6Reloading script: " + scriptName);
            
            plugin.getScriptEngine().reloadScript(scriptName)
                    .thenRun(() -> sender.sendMessage("§aScript reloaded: " + scriptName))
                    .exceptionally(ex -> {
                        sender.sendMessage("§cFailed to reload script: " + ex.getMessage());
                        return null;
                    });
        }
    }
    
    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /script info <name>");
            return;
        }
        
        String scriptName = args[1];
        var scriptOpt = plugin.getScriptEngine().getScript(scriptName);
        
        if (scriptOpt.isEmpty()) {
            sender.sendMessage("§cScript not found: " + scriptName);
            return;
        }
        
        var script = scriptOpt.get();
        
        sender.sendMessage("§6╔═══════════════════════════════════════╗");
        sender.sendMessage("§6║         Script Information            ║");
        sender.sendMessage("§6╚═══════════════════════════════════════╝");
        sender.sendMessage("§eID: §f" + script.getId());
        sender.sendMessage("§ePath: §f" + script.getPath());
        sender.sendMessage("§eExecutions: §f" + script.getExecutionCount());
        sender.sendMessage("§eStatus: " + (script.hasErrors() ? "§cError" : "§aOK"));
        
        if (script.hasErrors()) {
            script.getLastError().ifPresent(error -> {
                sender.sendMessage("§cLast error: §7" + error.getMessage());
            });
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6╔═══════════════════════════════════════╗");
        sender.sendMessage("§6║         Script Commands               ║");
        sender.sendMessage("§6╚═══════════════════════════════════════╝");
        sender.sendMessage("§e/script list §7- List all scripts");
        sender.sendMessage("§e/script reload [name] §7- Reload script(s)");
        sender.sendMessage("§e/script info <name> §7- Show script info");
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                     @NotNull String alias, @NotNull String[] args) {
        
        if (!sender.hasPermission("scriptslab.script")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("list", "reload", "info").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        
        if (args.length == 2 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("info"))) {
            return plugin.getScriptEngine().getAllScripts().stream()
                    .map(ScriptEngine.LoadedScript::getId)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        
        return new ArrayList<>();
    }
}
