/**
 * Приветственное сообщение при входе
 * Убирает ванильное сообщение и показывает кастомное
 */

API.registerEvent('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    
    // Убираем ванильное сообщение
    event.joinMessage(null);
    
    // Отправляем кастомное приветствие
    var Bukkit = Java.type('org.bukkit.Bukkit');
    
    // Приветствие игроку
    player.sendMessage('§8§m                                                  ');
    player.sendMessage('');
    player.sendMessage('  §6§l⚡ Добро пожаловать на сервер! ⚡');
    player.sendMessage('');
    player.sendMessage('  §7Привет, §e' + player.getName() + '§7!');
    player.sendMessage('  §7Онлайн: §a' + Bukkit.getOnlinePlayers().size() + ' §7игроков');
    player.sendMessage('');
    player.sendMessage('  §7Используй §e/help §7для помощи');
    player.sendMessage('');
    player.sendMessage('§8§m                                                  ');
    
    // Сообщение всем остальным
    Bukkit.getOnlinePlayers().forEach(function(p) {
        if (p.getName() !== player.getName()) {
            p.sendMessage('§8[§a+§8] §7' + player.getName() + ' §aзашёл на сервер');
        }
    });
    
    Console.log(player.getName() + ' joined the server');
});

// Кастомное сообщение при выходе
API.registerEvent('PlayerQuitEvent', function(event) {
    var player = event.getPlayer();
    
    // Убираем ванильное сообщение
    event.quitMessage(null);
    
    // Отправляем кастомное сообщение всем
    var Bukkit = Java.type('org.bukkit.Bukkit');
    Bukkit.getOnlinePlayers().forEach(function(p) {
        if (p.getName() !== player.getName()) {
            p.sendMessage('§8[§c-§8] §7' + player.getName() + ' §cвышел с сервера');
        }
    });
    
    Console.log(player.getName() + ' left the server');
});

Console.log('Приветственное сообщение активировано');
