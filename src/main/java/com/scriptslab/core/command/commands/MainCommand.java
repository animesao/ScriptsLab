package com.scriptslab.core.command.commands;

import com.scriptslab.ScriptsLabPlugin;
import com.scriptslab.core.config.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main ScriptsLab command.
 * /scriptslab <reload|info|help>
 */
public class MainCommand implements CommandExecutor, TabCompleter {
    
    private final ScriptsLabPlugin plugin;
    private final ConfigurationManager config;
    
    public MainCommand(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "info" -> handleInfo(sender);
            case "help" -> sendHelp(sender);
            default -> sendHelp(sender);
        }
        
        return true;
    }
    
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("scriptslab.reload")) {
            sender.sendMessage(config.getMessage("general.no-permission"));
            return;
        }
        
        if (plugin.getScriptEngine().wasPreviouslyLoaded()) {
            sender.sendMessage("§c⚠ Hot reload detected!");
            sender.sendMessage("§eScriptsLab uses GraalVM which is incompatible with hot reload.");
            sender.sendMessage("§ePlease restart the server using §f/stop §ethen start the server again.");
            sender.sendMessage("§eAlternatively use §f/restart §e(if available on your server).");
            
            ScriptsLabPlugin.setHotReloadAttempted(true);
            return;
        }
        
        sender.sendMessage("§6Reloading ScriptsLab...");
        
        plugin.reload().thenRun(() -> {
            sender.sendMessage("§aScriptsLab reloaded successfully!");
        }).exceptionally(ex -> {
            sender.sendMessage("§cError reloading: " + ex.getMessage());
            return null;
        });
    }
    
    private void handleInfo(CommandSender sender) {
        sender.sendMessage("§6╔═══════════════════════════════════════╗");
        sender.sendMessage("§6║         ScriptsLab Information        ║");
        sender.sendMessage("§6╚═══════════════════════════════════════╝");
        sender.sendMessage("§eVersion: §f" + plugin.getDescription().getVersion());
        sender.sendMessage("§eAuthors: §f" + String.join(", ", plugin.getDescription().getAuthors()));
        sender.sendMessage("");
        sender.sendMessage("§eModules loaded: §f" + plugin.getModuleManager().getModules().size());
        sender.sendMessage("§eModules enabled: §f" + plugin.getModuleManager().getEnabledModules().size());
        sender.sendMessage("§eScripts loaded: §f" + plugin.getScriptEngine().getAllScripts().size());
        sender.sendMessage("§eCustom items: §f" + plugin.getItemManager().getAllItems().size());
        sender.sendMessage("§eActive tasks: §f" + plugin.getTaskScheduler().getActiveTaskCount());
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6╔═══════════════════════════════════════╗");
        sender.sendMessage("§6║         ScriptsLab Commands           ║");
        sender.sendMessage("§6╚═══════════════════════════════════════╝");
        sender.sendMessage("§e/scriptslab reload §7- Reload the plugin");
        sender.sendMessage("§e/scriptslab info §7- Show plugin information");
        sender.sendMessage("§e/scriptslab help §7- Show this help");
        sender.sendMessage("");
        sender.sendMessage("§e/module list §7- List all modules");
        sender.sendMessage("§e/script list §7- List all scripts");
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                     @NotNull String alias, @NotNull String[] args) {
        
        if (args.length == 1) {
            return Arrays.asList("reload", "info", "help").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        
        return new ArrayList<>();
    }
}
