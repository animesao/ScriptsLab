/**
 * Пример: Обработка событий
 * Демонстрирует как слушать различные события сервера
 */

// Событие входа игрока
Events.on('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    Console.log('Игрок ' + player.getName() + ' зашёл на сервер');
});

// Событие выхода игрока
Events.on('PlayerQuitEvent', function(event) {
    var player = event.getPlayer();
    Console.log('Игрок ' + player.getName() + ' покинул сервер');
});

// Событие смерти игрока
Events.on('PlayerDeathEvent', function(event) {
    var player = event.getEntity();
    var killer = player.getKiller();
    
    if (killer !== null) {
        Console.log(player.getName() + ' был убит игроком ' + killer.getName());
    } else {
        Console.log(player.getName() + ' умер');
    }
});

// Событие чата
Events.on('AsyncPlayerChatEvent', function(event) {
    var player = event.getPlayer();
    var message = event.getMessage();
    
    // Фильтр плохих слов (пример)
    var badWords = ['плохоеслово1', 'плохоеслово2'];
    for (var i = 0; i < badWords.length; i++) {
        if (message.toLowerCase().indexOf(badWords[i]) !== -1) {
            event.setCancelled(true);
            player.sendMessage('§cПожалуйста, следите за своей речью!');
            return;
        }
    }
});

Console.log('Обработчики событий зарегистрированы');

