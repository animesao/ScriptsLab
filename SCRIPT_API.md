# ScriptsLab - Script API Documentation

## Доступные глобальные объекты

### Console / Logger
Логирование сообщений в консоль сервера.

```javascript
Console.log('Информационное сообщение');
Console.warn('Предупреждение');
Console.error('Ошибка');
Console.info('Информация');
Console.debug('Отладка');
```

### Commands
Регистрация команд.

```javascript
// Простая команда
Commands.register('mycommand', function(sender, args) {
    sender.sendMessage('§aПривет!');
});

// Команда с правами
Commands.register('admin', function(sender, args) {
    if (!sender.hasPermission('myplugin.admin')) {
        sender.sendMessage('§cНет прав!');
        return;
    }
    sender.sendMessage('§aВы админ!');
}, 'myplugin.admin');

// Проверка, что отправитель - игрок
Commands.register('playeronly', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    player.sendMessage('§aВы игрок: ' + player.getName());
});
```

### Events
Обработка событий Bukkit.

```javascript
// Вход игрока
Events.on('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    player.sendMessage('§aДобро пожаловать, ' + player.getName() + '!');
});

// Выход игрока
Events.on('PlayerQuitEvent', function(event) {
    var player = event.getPlayer();
    Console.log(player.getName() + ' вышел');
});

// Смерть игрока
Events.on('PlayerDeathEvent', function(event) {
    var player = event.getEntity();
    player.sendMessage('§cВы умерли!');
});

// Чат
Events.on('AsyncPlayerChatEvent', function(event) {
    var player = event.getPlayer();
    var message = event.getMessage();
    
    // Отменить событие
    if (message.contains('spam')) {
        event.setCancelled(true);
        player.sendMessage('§cНе спамьте!');
    }
});

// Урон
Events.on('EntityDamageByEntityEvent', function(event) {
    var damager = event.getDamager();
    var victim = event.getEntity();
    
    Console.log(damager.getName() + ' ударил ' + victim.getName());
});

// Стрельба из лука
Events.on('EntityShootBowEvent', function(event) {
    var shooter = event.getEntity();
    Console.log(shooter.getName() + ' выстрелил из лука');
});
```

### Scheduler
Планирование задач.

```javascript
// Выполнить через N тиков (20 тиков = 1 секунда)
Scheduler.runLater(function() {
    Server.broadcast('§eСообщение через 5 секунд!');
}, 100); // 100 тиков = 5 секунд

// Повторяющаяся задача
Scheduler.runTimer(function() {
    Server.broadcast('§eКаждую минуту!');
}, 0, 1200); // 0 = начать сразу, 1200 тиков = 60 секунд

// Асинхронная задача (не блокирует основной поток)
Scheduler.runAsync(function() {
    // Тяжёлые вычисления
    var result = heavyCalculation();
    
    // Вернуться в основной поток
    Scheduler.runLater(function() {
        Server.broadcast('§aРезультат: ' + result);
    }, 0);
});
```

### Players
Управление игроками.

```javascript
// Получить игрока по имени
var player = Players.get('PlayerName');
if (player !== null) {
    player.sendMessage('§aПривет!');
}

// Получить всех онлайн игроков
var players = Players.getAll();
for (var i = 0; i < players.length; i++) {
    players[i].sendMessage('§eСообщение всем!');
}

// Телепортация
Players.teleport(player, world, x, y, z);

// Дать предмет
Players.giveItem(player, 'custom_item_id', 1);
```

### Server
Управление сервером.

```javascript
// Отправить сообщение всем
Server.broadcast('§aСообщение всем игрокам!');

// Выполнить команду от имени консоли
Server.executeCommand('say Привет от скрипта!');

// Получить онлайн игроков
var online = Server.getOnlinePlayers();
Console.log('Онлайн: ' + online.length + ' игроков');
```

### World
Управление миром.

```javascript
// Получить мир
var world = World.get('world');

// Установить время
World.setTime(world, 1000); // День

// Установить погоду
World.setWeather(world, 'CLEAR'); // Ясно
World.setWeather(world, 'RAIN');  // Дождь
World.setWeather(world, 'THUNDER'); // Гроза

// Создать моба
World.spawnEntity(world, x, y, z, 'ZOMBIE');
World.spawnEntity(world, x, y, z, 'CREEPER');
```

### Items
Создание кастомных предметов.

```javascript
// Создать предмет
var sword = Items.create('fire_sword', 'DIAMOND_SWORD', '§cОгненный меч');
sword.setLore(['§7Поджигает врагов', '§cЛегендарный']);

// Добавить способность
sword.addAbility('HIT', function(player, target, item) {
    target.setFireTicks(100); // Поджечь на 5 секунд
    player.sendMessage('§cВраг горит!');
});

// Зарегистрировать предмет
Items.register(sword);
```

### Storage
Сохранение данных.

```javascript
// Сохранить данные
Storage.save('player.coins', 100);

// Загрузить данные
Storage.load('player.coins').then(function(coins) {
    Console.log('Монет: ' + coins);
});

// Удалить данные
Storage.delete('player.temp');

// Проверить существование
Storage.exists('player.coins').then(function(exists) {
    if (exists) {
        Console.log('Данные существуют');
    }
});
```

## Работа с Java классами

ScriptsLab позволяет использовать Java классы напрямую:

```javascript
// Импорт Java классов
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var ArrayList = Java.type('java.util.ArrayList');

// Создание объектов
var item = new ItemStack(Material.DIAMOND);
var list = new ArrayList();

// Использование методов
item.setAmount(64);
list.add('элемент');
```

## Примеры скриптов

### Пример 1: Команда телепортации

```javascript
Commands.register('spawn', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    var world = player.getWorld();
    
    // Телепорт на спавн (0, 100, 0)
    var Location = Java.type('org.bukkit.Location');
    var spawn = new Location(world, 0, 100, 0);
    
    player.teleport(spawn);
    player.sendMessage('§aВы телепортированы на спавн!');
});

Console.log('Команда /spawn зарегистрирована');
```

### Пример 2: Автоматические объявления

```javascript
var messages = [
    '§6Не забудьте проголосовать за сервер!',
    '§aПосетите наш сайт: example.com',
    '§eПрисоединяйтесь к Discord!'
];

var currentIndex = 0;

Scheduler.runTimer(function() {
    Server.broadcast(messages[currentIndex]);
    currentIndex = (currentIndex + 1) % messages.length;
}, 0, 6000); // Каждые 5 минут

Console.log('Автоматические объявления запущены');
```

### Пример 3: Кастомный предмет с эффектом

```javascript
// Создаём волшебную палочку
var wand = Items.create('magic_wand', 'STICK', '§5Волшебная палочка');
wand.setLore(['§7ПКМ - телепорт вперёд']);

wand.addAbility('RIGHT_CLICK', function(player, item) {
    var location = player.getLocation();
    var direction = location.getDirection();
    
    // Телепорт на 5 блоков вперёд
    direction.multiply(5);
    location.add(direction);
    
    player.teleport(location);
    player.sendMessage('§aВжух!');
    
    // Эффект
    player.playSound(player.getLocation(), 'ENTITY_ENDERMAN_TELEPORT', 1.0, 1.0);
});

Items.register(wand);
Console.log('Волшебная палочка создана!');
```

### Пример 4: Система уровней

```javascript
// При убийстве моба
Events.on('EntityDeathEvent', function(event) {
    var killer = event.getEntity().getKiller();
    if (killer === null) return;
    
    // Загрузить уровень игрока
    var key = 'player.' + killer.getUniqueId() + '.level';
    
    Storage.load(key).then(function(level) {
        var currentLevel = level || 1;
        var newLevel = currentLevel + 1;
        
        Storage.save(key, newLevel);
        killer.sendMessage('§aУровень: ' + newLevel);
    });
});

Console.log('Система уровней активирована');
```

## Отладка скриптов

### Логирование

```javascript
Console.log('Обычное сообщение');
Console.warn('Предупреждение');
Console.error('Ошибка');
Console.debug('Отладочная информация');
```

### Обработка ошибок

```javascript
try {
    // Ваш код
    var result = riskyOperation();
} catch (error) {
    Console.error('Ошибка: ' + error.message);
}
```

### Проверка типов

```javascript
if (typeof variable === 'undefined') {
    Console.warn('Переменная не определена');
}

if (object === null) {
    Console.warn('Объект null');
}
```

## Перезагрузка скриптов

Скрипты можно перезагружать без перезапуска сервера:

```
/script reload          # Перезагрузить все скрипты
/script reload hello    # Перезагрузить конкретный скрипт
/script list            # Список всех скриптов
/script info hello      # Информация о скрипте
```

## Лучшие практики

1. **Всегда проверяйте null**
   ```javascript
   var player = Players.get('Name');
   if (player !== null) {
       // Безопасно использовать
   }
   ```

2. **Используйте осмысленные имена**
   ```javascript
   // Плохо
   function f(p, a) { }
   
   // Хорошо
   function handleCommand(player, args) { }
   ```

3. **Логируйте важные события**
   ```javascript
   Console.log('Скрипт загружен');
   Console.log('Команда выполнена: ' + commandName);
   ```

4. **Обрабатывайте ошибки**
   ```javascript
   try {
       // Код
   } catch (e) {
       Console.error('Ошибка: ' + e);
   }
   ```

5. **Очищайте ресурсы**
   ```javascript
   // Отменяйте задачи при выгрузке
   var taskId = Scheduler.runTimer(function() {
       // ...
   }, 0, 20);
   
   // При выгрузке скрипта
   Scheduler.cancel(taskId);
   ```

## Ограничения

- Скрипты выполняются в песочнице (sandbox)
- Ограничен доступ к файловой системе
- Ограничен доступ к сети
- Таймаут выполнения: 5 секунд (настраивается)
- Максимальная память: 128MB (настраивается)

## Поддержка

- **Документация**: [README.md](README.md)
- **Примеры**: `plugins/ScriptsLab/scripts/examples/`
- **Логи**: `logs/latest.log`

---

**Удачного скриптинга! 🚀**
