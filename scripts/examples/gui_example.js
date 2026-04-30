// GUI API example
Console.log("GUI example loaded");

// Create a simple shop GUI
Commands.register('shop', function(sender) {
    if (!sender.isPlayer()) {
        sender.sendMessage("§cOnly players can use this!");
        return;
    }
    
    var player = org.bukkit.Bukkit.getPlayer(sender.getName());
    
    // Create GUI with title and size (27 slots = 3 rows)
    var shop = GUI.create('§6Server Shop', 27);
    
    // Add items with click handlers
    shop.setItem(0, 'DIAMOND', '§bDiamond §7- 100 coins', function(p) {
        p.sendMessage('§aYou bought a Diamond!');
        // Add actual purchase logic here
    }, '§7Click to buy');
    
    shop.setItem(1, 'GOLD_INGOT', '§6Gold §7- 50 coins', function(p) {
        p.sendMessage('§aYou bought Gold!');
    }, '§7Click to buy');
    
    shop.setItem(2, 'IRON_INGOT', '§7Iron §7- 25 coins', function(p) {
        p.sendMessage('§aYou bought Iron!');
    }, '§7Click to buy');
    
    shop.setItem(3, 'COAL', '§8Coal §7- 10 coins', function(p) {
        p.sendMessage('§aYou bought Coal!');
    }, '§7Click to buy');
    
    shop.setItem(4, 'EMERALD', '§aEmerald §7- 200 coins', function(p) {
        p.sendMessage('§aYou bought an Emerald!');
    }, '§7Click to buy');
    
    // Decorative filler
    shop.fill('GRAY_STAINED_GLASS_PANE', '§8 ');
    
    // Close handler
    shop.onClose(function(p) {
        Console.log(p.getName() + ' closed the shop');
    });
    
    // Open for player
    shop.open(player);
});

// Example: Player menu
Commands.register('menu', function(sender) {
    if (!sender.isPlayer()) return;
    
    var player = org.bukkit.Bukkit.getPlayer(sender.getName());
    var menu = GUI.create('§6§lYour Menu', 27);
    
    // Stats button
    menu.setItem(11, 'PAPER', '§e§lStats', function(p) {
        p.sendMessage('§6=== Your Stats ===');
        p.sendMessage('§7Health: ' + p.getHealth() + '/' + p.getMaxHealth());
        p.sendMessage('§7Food: ' + p.getFoodLevel());
        p.closeInventory();
    }, '§7View your stats');
    
    // Teleport button
    menu.setItem(13, 'COMPASS', '§b§lSpawn', function(p) {
        var spawn = p.getWorld().getSpawnLocation();
        p.teleport(spawn);
        p.sendMessage('§aTeleported to spawn!');
        p.closeInventory();
    }, '§7Return to spawn');
    
    // Heal button
    menu.setItem(15, 'REDSTONE', '§c§lHeal', function(p) {
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.sendMessage('§aHealed!');
        p.closeInventory();
    }, '§7Restore health & food');
    
    menu.fill('BLACK_STAINED_GLASS_PANE', '§8 ');
    
    menu.open(player);
});

// Example: Confirm dialog
function showConfirmDialog(player, title, message, onConfirm, onCancel) {
    var dialog = GUI.create(title, 18);
    
    dialog.setItem(2, 'GREEN_WOOL', '§a§lConfirm', function(p) {
        onConfirm(p);
    }, message);
    
    dialog.setItem(6, 'RED_WOOL', '§c§lCancel', function(p) {
        if (onCancel) onCancel(p);
    }, '§7Click to cancel');
    
    dialog.fill('GRAY_STAINED_GLASS_PANE', '§8 ');
    dialog.open(player);
}

// Usage example
Commands.register('testconfirm', function(sender) {
    if (!sender.isPlayer()) return;
    var player = org.bukkit.Bukkit.getPlayer(sender.getName());
    
    showConfirmDialog(
        player,
        '§6Confirm Action',
        '§7Are you sure?',
        function(p) {
            p.sendMessage('§aConfirmed!');
            p.closeInventory();
        },
        function(p) {
            p.sendMessage('§cCancelled');
            p.closeInventory();
        }
    );
});