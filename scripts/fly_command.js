/**
 * Команда /fly - Включение/выключение полёта
 */

Commands.register('fly', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    var currentFlight = player.getAllowFlight();
    player.setAllowFlight(!currentFlight);
    
    var msg = player.getAllowFlight() ? '§a✓ Полёт включен!' : '§c✗ Полёт выключен!';
    player.sendMessage(msg);
    
    Console.log(player.getName() + ' toggled flight: ' + player.getAllowFlight());
}, 'scriptslab.fly');

Console.log('Команда /fly зарегистрирована');
