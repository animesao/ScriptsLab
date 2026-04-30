/**
 * Команда /heal - Восстановление здоровья
 */

Commands.register('heal', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    
    // Восстановить здоровье
    player.setHealth(player.getMaxHealth());
    
    // Восстановить голод
    player.setFoodLevel(20);
    player.setSaturation(20.0);
    
    // Убрать эффекты
    player.getActivePotionEffects().forEach(function(effect) {
        player.removePotionEffect(effect.getType());
    });
    
    player.sendMessage('§a✓ Вы полностью исцелены!');
    Console.log(player.getName() + ' used /heal');
    
}, 'scriptslab.heal');

Console.log('Команда /heal зарегистрирована');
