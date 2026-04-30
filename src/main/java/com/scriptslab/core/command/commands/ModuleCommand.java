package com.scriptslab.core.command.commands;

import com.scriptslab.ScriptsLabPlugin;
import com.scriptslab.api.module.Module;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Module management command.
 * /module <list|enable|disable|reload> [name]
 */
public class ModuleCommand implements CommandExecutor, TabCompleter {
    
    private final ScriptsLabPlugin plugin;
    
    public ModuleCommand(ScriptsLabPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("scriptslab.module")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "list" -> handleList(sender);
            case "enable" -> handleEnable(sender, args);
            case "disable" -> handleDisable(sender, args);
            case "reload" -> handleReload(sender, args);
            default -> sendHelp(sender);
        }
        
        return true;
    }
    
    private void handleList(CommandSender sender) {
        var modules = plugin.getModuleManager().getModules();
        
        sender.sendMessage("§6╔═══════════════════════════════════════╗");
        sender.sendMessage("§6║           Loaded Modules              ║");
        sender.sendMessage("§6╚═══════════════════════════════════════╝");
        
        if (modules.isEmpty()) {
            sender.sendMessage("§7No modules loaded");
            return;
        }
        
        for (Module module : modules) {
            String status = module.isEnabled() ? "§a✓ Enabled" : "§c✗ Disabled";
            sender.sendMessage(String.format("§e%s §7v%s %s", 
                    module.getName(), module.getVersion(), status));
        }
        
        sender.sendMessage("");
        sender.sendMessage("§7Total: §e" + modules.size() + " §7modules");
    }
    
    private void handleEnable(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /module enable <name>");
            return;
        }
        
        String moduleName = args[1];
        
        if (!plugin.getModuleManager().isLoaded(moduleName)) {
            sender.sendMessage("§cModule not found: " + moduleName);
            return;
        }
        
        if (plugin.getModuleManager().isEnabled(moduleName)) {
            sender.sendMessage("§eModule is already enabled: " + moduleName);
            return;
        }
        
        sender.sendMessage("§6Enabling module: " + moduleName);
        
        plugin.getModuleManager().enableModule(moduleName)
                .thenRun(() -> sender.sendMessage("§aModule enabled: " + moduleName))
                .exceptionally(ex -> {
                    sender.sendMessage("§cFailed to enable module: " + ex.getMessage());
                    return null;
                });
    }
    
    private void handleDisable(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /module disable <name>");
            return;
        }
        
        String moduleName = args[1];
        
        if (!plugin.getModuleManager().isLoaded(moduleName)) {
            sender.sendMessage("§cModule not found: " + moduleName);
            return;
        }
        
        if (!plugin.getModuleManager().isEnabled(moduleName)) {
            sender.sendMessage("§eModule is already disabled: " + moduleName);
            return;
        }
        
        sender.sendMessage("§6Disabling module: " + moduleName);
        
        plugin.getModuleManager().disableModule(moduleName)
                .thenRun(() -> sender.sendMessage("§aModule disabled: " + moduleName))
                .exceptionally(ex -> {
                    sender.sendMessage("§cFailed to disable module: " + ex.getMessage());
                    return null;
                });
    }
    
    private void handleReload(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /module reload <name>");
            return;
        }
        
        String moduleName = args[1];
        
        if (!plugin.getModuleManager().isLoaded(moduleName)) {
            sender.sendMessage("§cModule not found: " + moduleName);
            return;
        }
        
        sender.sendMessage("§6Reloading module: " + moduleName);
        
        plugin.getModuleManager().reloadModule(moduleName)
                .thenRun(() -> sender.sendMessage("§aModule reloaded: " + moduleName))
                .exceptionally(ex -> {
                    sender.sendMessage("§cFailed to reload module: " + ex.getMessage());
                    return null;
                });
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6╔═══════════════════════════════════════╗");
        sender.sendMessage("§6║         Module Commands               ║");
        sender.sendMessage("§6╚═══════════════════════════════════════╝");
        sender.sendMessage("§e/module list §7- List all modules");
        sender.sendMessage("§e/module enable <name> §7- Enable a module");
        sender.sendMessage("§e/module disable <name> §7- Disable a module");
        sender.sendMessage("§e/module reload <name> §7- Reload a module");
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                     @NotNull String alias, @NotNull String[] args) {
        
        if (!sender.hasPermission("scriptslab.module")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("list", "enable", "disable", "reload").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        
        if (args.length == 2 && !args[0].equalsIgnoreCase("list")) {
            return new ArrayList<>(plugin.getModuleManager().getModules().stream()
                    .map(Module::getId)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList());
        }
        
        return new ArrayList<>();
    }
}
