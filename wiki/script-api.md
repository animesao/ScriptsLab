# 📜 Script API

Полный справочник по JavaScript API для ScriptsLab. Здесь описаны все доступные объекты и методы для создания скриптов.

---

## Глобальные объекты

При запуске скрипта вам доступны следующие глобальные объекты:

| Объект | Описание |
|--------|---------|
| `Console` | Логирование в консоль сервера |
| `Commands` | Регистрация команд |
| `Events` | Обработка событий |
| `Scheduler` | Планирование задач |
| `Players` | Управление игроками |
| `Server` | Управление сервером |
| `World` | Управление мирами |
| `Items` | Создание предметов |
| `Storage` | Сохранение данных |
| `API` | Расширенный API |

---

## Console - Логирование

Вывод сообщений в консоль сервера.

### Методы

```javascript
// Информационное сообщение
Console.log('Сообщение');

// Предупреждение
Console.warn('Предупреждение');

// Ошибка
Console.error('Ошибка');

// Отладочное сообщение (только при debug: true)
Console.debug('Отладка');
```

### Пример

```javascript
Console.log('Скрипт загружен');
Console.warn('Внимание: что-то не так');
Console.error('Произошла ошибка: ' + errorMessage);
Console.debug('Переменная x = ' + x);
```

---

## Commands - Команды

Регистрация и управление командами сервера.

### Регистрация команды

```javascript
Commands.register(name, handler, permission);
```

| Параметр | Тип | Описание |
|----------|-----|---------|
| `name` | string | Имя команды |
| `handler` | function | Функция-обработчик |
| `permission` | string | Право доступа (опционально) |

### Обработчик команды

```javascript
function handler(sender, args) {
    // sender - отправитель команды
    // args - массив аргументов
}
```

### Примеры

#### Простая команда

```javascript
Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aПривет, ' + sender.getName() + '!');
});
```

#### Команда с правами

```javascript
Commands.register('heal', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    player.setHealth(player.getMaxHealth());
    player.sendMessage('§aВы исцелены!');
}, 'scriptslab.heal');
```

#### Команда с аргументами

```javascript
Commands.register('warp', function(sender, args) {
    if (args.length === 0) {
        sender.sendMessage('§cИспользование: /warp <название>');
        return;
    }
    
    var warpName = args[0];
    // Логика телепортации
    sender.sendMessage('§aТелепорт на §e' + warpName);
});
```

#### Команда с проверкой игрока

```javascript
Commands.register('fly', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    var canFly = player.getAllowFlight();
    player.setAllowFlight(!canFly);
    
    var msg = !canFly ? '§aПолёт включен!' : '§cПолёт выключен!';
    player.sendMessage(msg);
}, 'scriptslab.fly');
```

---

## Events - События

Обработка событий Bukkit.

### Подписка на событие

```javascript
Events.on(eventName, handler);
```

| Параметр | Тип | Описание |
|----------|-----|---------|
| `eventName` | string | Имя события (например, PlayerJoinEvent) |
| `handler` | function | Функция-обработчик |

### Обработчик события

```javascript
function handler(event) {
    // event - объект события Bukkit
}
```

### Доступные события

#### События игрока

| Событие | Описание |
|---------|---------|
| `PlayerJoinEvent` | Вход на сервер |
| `PlayerQuitEvent` | Выход с сервера |
| `PlayerChatEvent` | Чат (синхронный) |
| `AsyncPlayerChatEvent` | Чат (асинхронный) |
| `PlayerMoveEvent` | Движение |
| `PlayerInteractEvent` | Взаимодействие |
| `PlayerItemHeldEvent` | Смена предмета в руке |
| `PlayerDeathEvent` | Смерть |
| `PlayerRespawnEvent` | Возрождение |

#### События сущностей

| Событие | Описание |
|---------|---------|
| `EntityDamageEvent` | Получение урона |
| `EntityDamageByEntityEvent` | Урон от сущности |
| `EntityDeathEvent` | Смерть сущности |
| `EntityShootBowEvent` | Выстрел из лука |

#### События блоков

| Событие | Описание |
|---------|---------|
| `BlockPlaceEvent` | Установка блока |
| `BlockBreakEvent` | Разрушение блока |
| `BlockInteractEvent` | Взаимодействие с блоком |

### Примеры

#### Вход игрока

```javascript
Events.on('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    player.sendMessage('§aДобро пожаловать!');
    Console.log(player.getName() + ' зашёл на сервер');
});
```

#### Выход игрока

```javascript
Events.on('PlayerQuitEvent', function(event) {
    var player = event.getPlayer();
    Console.log(player.getName() + ' вышел с сервера');
});
```

#### Чат

```javascript
Events.on('AsyncPlayerChatEvent', function(event) {
    var player = event.getPlayer();
    var message = event.getMessage();
    
    // Фильтр плохих слов
    if (message.toLowerCase().indexOf('badword') !== -1) {
        event.setCancelled(true);
        player.sendMessage('§cТакое говорить нельзя!');
        return;
    }
});
```

#### Смерть игрока

```javascript
Events.on('PlayerDeathEvent', function(event) {
    var entity = event.getEntity();
    var killer = entity.getKiller();
    
    if (killer !== null) {
        Console.log(entity.getName() + ' был убит ' + killer.getName());
    }
});
```

#### Урон от игрока

```javascript
Events.on('EntityDamageByEntityEvent', function(event) {
    var damager = event.getDamager();
    var victim = event.getEntity();
    
    // Проверяем, что атаковал игрок
    var Player = Java.type('org.bukkit.entity.Player');
    if (damager instanceof Player) {
        Console.log(damager.getName() + ' атаковал ' + victim.getName());
    }
});
```

---

## Scheduler - Планировщик

Планирование отложенных и повторяющихся задач.

> **Важно**: Все методы Bukkit API автоматически выполняются в главном потоке!

### Методы

#### runLater - Выполнить один раз

```javascript
Scheduler.runLater(handler, delay);
```

| Параметр | Тип | Описание |
|----------|-----|---------|
| `handler` | function | Функция для выполнения |
| `delay` | integer | Задержка в тиках (20 тиков = 1 секунда) |

```javascript
// Выполнить через 5 секунд (100 тиков)
Scheduler.runLater(function() {
    player.sendMessage('§aСообщение через 5 секунд!');
}, 100);
```

#### runTimer - Повторяющаяся задача

```javascript
Scheduler.runTimer(handler, delay, period);
```

| Параметр | Тип | Описание |
|----------|-----|---------|
| `handler` | function | Функция для выполнения |
| `delay` | integer | Начальная задержка |
| `period` | integer | Интервал повторения |

```javascript
// Выполнять каждую минуту (1200 тиков)
Scheduler.runTimer(function() {
    Server.broadcast('§eНе забудьте проголосовать!');
}, 0, 1200);
```

### Примеры

#### Отложенное сообщение

```javascript
Scheduler.runLater(function() {
    Server.broadcast('§eСервер перезагрузится через 10 минут!');
}, 12000); // 12000 тиков = 10 минут
```

#### Повторяющееся объявление

```javascript
var messages = [
    '§eНе забудьте проголосовать!',
    '§aПрисоединяйтесь к Discord!',
    '§6Голосуйте за сервер!'
];

var index = 0;

Scheduler.runTimer(function() {
    Server.broadcast(messages[index]);
    index = (index + 1) % messages.length;
}, 0, 6000); // Каждые 5 минут
```

#### Таймер с отменой

```javascript
// Сохраняем ID задачи
var taskId = Scheduler.runTimer(function() {
    // Код
}, 0, 20);

// Отмена задачи
// Scheduler.cancel(taskId);
```

---

## Players - Игроки

Управление игроками.

### Методы

#### get - Получить игрока

```javascript
var player = Players.get('NickName');
```

| Параметр | Тип | Описание |
|----------|-----|---------|
| `name` | string | Имя игрока |

```javascript
var player = Players.get('Steve');
if (player !== null) {
    player.sendMessage('§aПривет!');
}
```

#### getAll - Все онлайн игроки

```javascript
var players = Players.getAll();
```

```javascript
var players = Players.getAll();
for (var i = 0; i < players.length; i++) {
    players[i].sendMessage('§eОбъявление для всех!');
}
```

#### teleport - Телепортация

```javascript
Players.teleport(player, world, x, y, z);
```

```javascript
var player = Players.get('Steve');
var world = org.bukkit.Bukkit.getWorld('world');
Players.teleport(player, world, 0, 100, 0);
```

#### giveItem - Дать предмет

```javascript
Players.giveItem(player, itemId, amount);
```

```javascript
var player = Players.get('Steve');
Players.giveItem(player, 'custom_sword', 1);
```

---

## Server - Сервер

Управление сервером.

### Методы

#### broadcast - Сообщение всем

```javascript
Server.broadcast(message);
```

```javascript
Server.broadcast('§aПривет всем игрокам!');
```

#### executeCommand - Выполнить команду

```javascript
Server.executeCommand(command);
```

```javascript
Server.executeCommand('say Сообщение от сервера!');
Server.executeCommand('give Steve DIAMOND 1');
```

#### getOnlinePlayers - Онлайн игроки

```javascript
var players = Server.getOnlinePlayers();
```

```javascript
var count = Server.getOnlinePlayers().size();
Console.log('Онлайн: ' + count);
```

---

## World - Мир

Управление мирами.

### Методы

#### get - Получить мир

```javascript
var world = World.get('world_name');
```

```javascript
var world = World.get('world');
```

#### setTime - Установить время

```javascript
World.setTime(world, time);
```

```javascript
var world = World.get('world');
World.setTime(world, 1000); // День (0-24000)
```

#### setWeather - Установить погоду

```javascript
World.setWeather(world, weather);
```

```javascript
var world = World.get('world');
World.setWeather(world, 'CLEAR');  // Ясно
World.setWeather(world, 'RAIN');   // Дождь
World.setWeather(world, 'THUNDER'); // Гроза
```

#### spawnEntity - Создать сущность

```javascript
World.spawnEntity(world, x, y, z, entityType);
```

```javascript
var world = World.get('world');
World.spawnEntity(world, 0, 100, 0, 'ZOMBIE');
World.spawnEntity(world, 0, 100, 0, 'CREEPER');
```

---

## Items - Предметы

Создание кастомных предметов.

### Методы

#### create - Создать предмет

```javascript
var item = Items.create(id, material, displayName);
```

| Параметр | Тип | Описание |
|----------|-----|---------|
| `id` | string | Уникальный ID |
| `material` | string | Материал (например, DIAMOND_SWORD) |
| `displayName` | string | Отображаемое имя |

```javascript
var sword = Items.create('fire_sword', 'DIAMOND_SWORD', '§cОгненный меч');
sword.setLore(['§7Поджигает врагов']);
```

#### setLore - Установить описание

```javascript
item.setLore(loreArray);
```

```javascript
var sword = Items.create('fire_sword', 'DIAMOND_SWORD', '§cОгненный меч');
sword.setLore([
    '§7Поджигает врагов',
    '§cЛегендарный предмет'
]);
```

#### addAbility - Добавить способность

```javascript
item.addAbility(triggerType, handler);
```

| triggerType | Описание |
|-------------|----------|
| `HIT` | При ударе |
| `RIGHT_CLICK` | Правый клик |
| `LEFT_CLICK` | Левый клик |

```javascript
var sword = Items.create('fire_sword', 'DIAMOND_SWORD', '§cОгненный меч');
sword.setLore(['§7Поджигает врагов']);

sword.addAbility('HIT', function(player, target, item) {
    target.setFireTicks(100); // Поджечь на 5 секунд
    player.sendMessage('§cВраг горит!');
});

Items.register(sword);
```

#### register - Зарегистрировать предмет

```javascript
Items.register(item);
```

```javascript
Items.register(sword);
```

---

## Storage - Хранилище

Сохранение и загрузка данных.

### Методы

#### save - Сохранить данные

```javascript
Storage.save(key, value);
```

```javascript
Storage.save('player.steve.coins', 100);
Storage.save('player.steve.level', 5);
```

#### load - Загрузить данные

```javascript
Storage.load(key).then(function(value) {
    // value - загруженное значение
});
```

```javascript
Storage.load('player.steve.coins').then(function(coins) {
    if (coins === null) {
        coins = 0;
    }
    Console.log('Монет: ' + coins);
});
```

#### delete - Удалить данные

```javascript
Storage.delete(key);
```

```javascript
Storage.delete('player.steve.temp');
```

#### exists - Проверить существование

```javascript
Storage.exists(key).then(function(exists) {
    if (exists) {
        Console.log('Данные существуют');
    }
});
```

---

## API - Расширенный API

Дополнительные методы для удобной работы.

### Методы

#### registerEvent - Регистрация события

```javascript
API.registerEvent(eventName, handler);
```

```javascript
API.registerEvent('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    player.sendMessage('§aДобро пожаловать!');
});
```

#### broadcast - Сообщение всем

```javascript
API.broadcast(message);
```

```javascript
API.broadcast('§eОбъявление для всех!');
```

#### getOnlinePlayers - Получить игроков

```javascript
var players = API.getOnlinePlayers();
```

```javascript
var count = API.getOnlinePlayers().size();
```

#### strikeLightningSync - Молния (безопасно)

```javascript
API.strikeLightningSync(location);
```

```javascript
API.strikeLightningSync(player.getLocation());
```

#### addPotionEffectSync - Эффект зелья (безопасно)

```javascript
API.addPotionEffectSync(player, effectType, duration, amplifier, ambient, particles);
```

```javascript
var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
API.addPotionEffectSync(player, PotionEffectType.SPEED, 999999, 2, false, false);
```

#### removePotionEffectSync - Убрать эффект (безопасно)

```javascript
API.removePotionEffectSync(player, effectType);
```

```javascript
var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
API.removePotionEffectSync(player, PotionEffectType.SPEED);
```

#### addAttribute - Атрибут предмета

```javascript
API.addAttribute(itemMeta, attributeName, modifierName, value, operation, slot);
```

| Параметр | Описани�� |
|----------|---------|
| `itemMeta` | ItemMeta предмета |
| `attributeName` | GENERIC_ATTACK_DAMAGE, GENERIC_ATTACK_SPEED и т.д. |
| `modifierName` | Уникальное имя модификатора |
| `value` | Значение |
| `operation` | ADD_NUMBER, MULTIPLY_SCALAR_1 |
| `slot` | HAND, FEET, LEGS, etc. |

```javascript
var Attribute = Java.type('org.bukkit.attribute.Attribute');
var AttributeModifier = Java.type('org.bukkit.attribute.AttributeModifier');
var UUID = Java.type('java.util.UUID');

API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'damage_boost', 10.0, 'ADD_NUMBER', 'HAND');
```

#### log - Логирование

```javascript
API.log(message);
```

```javascript
API.log('Скрипт загружен');
```

---

## Работа с Java

ScriptsLab позволяет использовать Java классы напрямую.

### Импорт классов

```javascript
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var Enchantment = Java.type('org.bukkit.enchantments.Enchantment');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');
var Attribute = Java.type('org.bukkit.attribute.Attribute');
var AttributeModifier = Java.type('org.bukkit.attribute.AttributeModifier');
var EquipmentSlot = Java.type('org.bukkit.inventory.EquipmentSlot');
var UUID = Java.type('java.util.UUID');
var ArrayList = Java.type('java.util.ArrayList');
var PotionEffect = Java.type('org.bukkit.potion.PotionEffect');
var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
```

### Создание объектов

```javascript
var item = new ItemStack(Material.DIAMOND_SWORD);
item.setAmount(64);

var list = new ArrayList();
list.add('элемент');
```

### Использование методов

```javascript
item.setAmount(64);
list.add('элемент');
var size = list.size();
```

### instanceof - Проверка типа

```javascript
var Player = Java.type('org.bukkit.entity.Player');
if (entity instanceof Player) {
    // Это игрок
}
```

---

## Работа с отправителем команды

### Типы отправителя

| Метод | Описание |
|--------|---------|
| `sender.isPlayer()` | Проверить, является ли игроком |
| `sender.isOp()` | Проверить, является ли OP |

### Примеры

```javascript
Commands.register('test', function(sender, args) {
    // Проверка игрок ли
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    player.sendMessage('§aПривет, игрок!');
});
```

### Получение игрока из отправителя

```javascript
var player = sender; // Если проверено isPlayer()
```

---

## Обработка ошибок

### try-catch

```javascript
try {
    // Ваш код
    var result = dangerousOperation();
} catch (error) {
    Console.error('Ошибка: ' + error);
}
```

### Проверка на null

```javascript
var player = Players.get('Steve');
if (player !== null) {
    // Безопасно использовать
} else {
    Console.warn('Игрок не найден');
}
```

---

## Типы данных

### JavaScript → Java

| JavaScript | Java |
|-----------|------|
| `number` | int, double, float |
| `string` | String |
| `boolean` | boolean |
| `array` | ArrayList, List |
| `function` | Consumer, Function |
| `object` | Object |

### Java → JavaScript

| Java | JavaScript |
|------|----------|
| `int` | number |
| `double` | number |
| `boolean` | boolean |
| `String` | string |
| `List` | array |
| `Map` | object |
| `Object` | object |

---

## Ограничения

- **Таймаут**: 5 секунд по умолчанию (настраивается)
- **Память**: 128MB на скрипт (настраивается)
- **Песочница**: Ограниченный доступ к Java API (при sandbox: true)
- **Потоки**: GraalVM JS однопоточный

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Примеры](examples/) | Готовые примеры скриптов |
| [Модули](modules.md) | Система модулей |
| [Безопасность](configuration.md#security) | Настройка безопасности |

---

# 📜 Script API (English)

Complete reference for ScriptsLab JavaScript API. All available objects and methods for creating scripts are described here.

---

## Global Objects

When a script starts, the following global objects are available:

| Object | Description |
|--------|---------|
| `Console` | Log to server console |
| `Commands` | Register commands |
| `Events` | Handle events |
| `Scheduler` | Task scheduling |
| `Players` | Player management |
| `Server` | Server management |
| `World` | World management |
| `Items` | Create items |
| `Storage` | Save data |
| `API` | Extended API |

---

## Console - Logging

Output messages to server console.

### Methods

```javascript
// Info message
Console.log('Message');

// Warning
Console.warn('Warning');

// Error
Console.error('Error');

// Debug message (only when debug: true)
Console.debug('Debug');
```

### Example

```javascript
Console.log('Script loaded');
Console.warn('Attention: something is wrong');
Console.error('An error occurred: ' + errorMessage);
Console.debug('Variable x = ' + x);
```

---

## Commands - Commands

Register and manage server commands.

### Register Command

```javascript
Commands.register(name, handler, permission);
```

| Parameter | Type | Description |
|----------|-----|---------|
| `name` | string | Command name |
| `handler` | function | Handler function |
| `permission` | string | Access permission (optional) |

### Command Handler

```javascript
function handler(sender, args) {
    // sender - command sender
    // args - argument array
}
```

### Examples

#### Simple Command

```javascript
Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aHello, ' + sender.getName() + '!');
});
```

#### Command with Permissions

```javascript
Commands.register('heal', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    player.setHealth(player.getMaxHealth());
    player.sendMessage('§aYou have been healed!');
}, 'scriptslab.heal');
```

#### Command with Arguments

```javascript
Commands.register('warp', function(sender, args) {
    if (args.length === 0) {
        sender.sendMessage('§cUsage: /warp <name>');
        return;
    }
    
    var warpName = args[0];
    // Teleportation logic
    sender.sendMessage('§aTeleporting to §e' + warpName);
});
```

#### Command with Player Check

```javascript
Commands.register('fly', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    var canFly = player.getAllowFlight();
    player.setAllowFlight(!canFly);
    
    var msg = !canFly ? '§aFlight enabled!' : '§cFlight disabled!';
    player.sendMessage(msg);
}, 'scriptslab.fly');
```

---

## Events - Events

Handle Bukkit events.

### Subscribe to Event

```javascript
Events.on(eventName, handler);
```

| Parameter | Type | Description |
|----------|-----|---------|
| `eventName` | string | Event name (e.g., PlayerJoinEvent) |
| `handler` | function | Handler function |

### Event Handler

```javascript
function handler(event) {
    // event - Bukkit event object
}
```

### Available Events

#### Player Events

| Event | Description |
|---------|---------|
| `PlayerJoinEvent` | Player join |
| `PlayerQuitEvent` | Player quit |
| `PlayerChatEvent` | Chat (sync) |
| `AsyncPlayerChatEvent` | Chat (async) |
| `PlayerMoveEvent` | Movement |
| `PlayerInteractEvent` | Interaction |
| `PlayerItemHeldEvent` | Item held change |
| `PlayerDeathEvent` | Death |
| `PlayerRespawnEvent` | Respawn |

#### Entity Events

| Event | Description |
|---------|---------|
| `EntityDamageEvent` | Taking damage |
| `EntityDamageByEntityEvent` | Damage by entity |
| `EntityDeathEvent` | Entity death |
| `EntityShootBowEvent` | Bow shoot |

#### Block Events

| Event | Description |
|---------|---------|
| `BlockPlaceEvent` | Block place |
| `BlockBreakEvent` | Block break |
| `BlockInteractEvent` | Block interaction |

### Examples

#### Player Join

```javascript
Events.on('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    player.sendMessage('§aWelcome!');
    Console.log(player.getName() + ' joined the server');
});
```

#### Player Quit

```javascript
Events.on('PlayerQuitEvent', function(event) {
    var player = event.getPlayer();
    Console.log(player.getName() + ' left the server');
});
```

#### Chat

```javascript
Events.on('AsyncPlayerChatEvent', function(event) {
    var player = event.getPlayer();
    var message = event.getMessage();
    
    // Bad word filter
    if (message.toLowerCase().indexOf('badword') !== -1) {
        event.setCancelled(true);
        player.sendMessage('§cYou can't say that!');
        return;
    }
});
```

#### Player Death

```javascript
Events.on('PlayerDeathEvent', function(event) {
    var entity = event.getEntity();
    var killer = entity.getKiller();
    
    if (killer !== null) {
        Console.log(entity.getName() + ' was killed by ' + killer.getName());
    }
});
```

#### Player Damage

```javascript
Events.on('EntityDamageByEntityEvent', function(event) {
    var damager = event.getDamager();
    var victim = event.getEntity();
    
    // Check if attacker is player
    var Player = Java.type('org.bukkit.entity.Player');
    if (damager instanceof Player) {
        Console.log(damager.getName() + ' attacked ' + victim.getName());
    }
});
```

---

## Scheduler - Task Scheduler

Schedule delayed and repeating tasks.

> **Important**: All Bukkit API methods are automatically executed on the main thread!

### Methods

#### runLater - Execute Once

```javascript
Scheduler.runLater(handler, delay);
```

| Parameter | Type | Description |
|----------|-----|---------|
| `handler` | function | Function to execute |
| `delay` | integer | Delay in ticks (20 ticks = 1 second) |

```javascript
// Execute after 5 seconds (100 ticks)
Scheduler.runLater(function() {
    player.sendMessage('§aMessage after 5 seconds!');
}, 100);
```

#### runTimer - Repeating Task

```javascript
Scheduler.runTimer(handler, delay, period);
```

| Parameter | Type | Description |
|----------|-----|---------|
| `handler` | function | Function to execute |
| `delay` | integer | Initial delay |
| `period` | integer | Repeat interval |

```javascript
// Execute every minute (1200 ticks)
Scheduler.runTimer(function() {
    Server.broadcast('§eDon\'t forget to vote!');
}, 0, 1200);
```

### Examples

#### Delayed Message

```javascript
Scheduler.runLater(function() {
    Server.broadcast('§eServer will restart in 10 minutes!');
}, 12000); // 12000 ticks = 10 minutes
```

#### Repeating Announcement

```javascript
var messages = [
    '§eDon\'t forget to vote!',
    '§aJoin our Discord!',
    '§6Vote for the server!'
];

var index = 0;

Scheduler.runTimer(function() {
    Server.broadcast(messages[index]);
    index = (index + 1) % messages.length;
}, 0, 6000); // Every 5 minutes
```

#### Timer with Cancel

```javascript
// Save task ID
var taskId = Scheduler.runTimer(function() {
    // Code
}, 0, 20);

// Cancel task
// Scheduler.cancel(taskId);
```

---

## Players - Players

Player management.

### Methods

#### get - Get Player

```javascript
var player = Players.get('NickName');
```

| Parameter | Type | Description |
|----------|-----|---------|
| `name` | string | Player name |

```javascript
var player = Players.get('Steve');
if (player !== null) {
    player.sendMessage('§aHello!');
}
```

#### getAll - All Online Players

```javascript
var players = Players.getAll();
```

```javascript
var players = Players.getAll();
for (var i = 0; i < players.length; i++) {
    players[i].sendMessage('§eAnnouncement for everyone!');
}
```

#### teleport - Teleport

```javascript
Players.teleport(player, world, x, y, z);
```

```javascript
var player = Players.get('Steve');
var world = org.bukkit.Bukkit.getWorld('world');
Players.teleport(player, world, 0, 100, 0);
```

#### giveItem - Give Item

```javascript
Players.giveItem(player, itemId, amount);
```

```javascript
var player = Players.get('Steve');
Players.giveItem(player, 'custom_sword', 1);
```

---

## Server - Server

Server management.

### Methods

#### broadcast - Message Everyone

```javascript
Server.broadcast(message);
```

```javascript
Server.broadcast('§aHello to all players!');
```

#### executeCommand - Execute Command

```javascript
Server.executeCommand(command);
```

```javascript
Server.executeCommand('say Message from server!');
Server.executeCommand('give Steve DIAMOND 1');
```

#### getOnlinePlayers - Online Players

```javascript
var players = Server.getOnlinePlayers();
```

```javascript
var count = Server.getOnlinePlayers().size();
Console.log('Online: ' + count);
```

---

## World - World

World management.

### Methods

#### get - Get World

```javascript
var world = World.get('world_name');
```

```javascript
var world = World.get('world');
```

#### setTime - Set Time

```javascript
World.setTime(world, time);
```

```javascript
var world = World.get('world');
World.setTime(world, 1000); // Day (0-24000)
```

#### setWeather - Set Weather

```javascript
World.setWeather(world, weather);
```

```javascript
var world = World.get('world');
World.setWeather(world, 'CLEAR');  // Clear
World.setWeather(world, 'RAIN');   // Rain
World.setWeather(world, 'THUNDER'); // Thunder
```

#### spawnEntity - Spawn Entity

```javascript
World.spawnEntity(world, x, y, z, entityType);
```

```javascript
var world = World.get('world');
World.spawnEntity(world, 0, 100, 0, 'ZOMBIE');
World.spawnEntity(world, 0, 100, 0, 'CREEPER');
```

---

## Items - Items

Create custom items.

### Methods

#### create - Create Item

```javascript
var item = Items.create(id, material, displayName);
```

| Parameter | Type | Description |
|----------|-----|---------|
| `id` | string | Unique ID |
| `material` | string | Material (e.g., DIAMOND_SWORD) |
| `displayName` | string | Display name |

```javascript
var sword = Items.create('fire_sword', 'DIAMOND_SWORD', '§cFire Sword');
sword.setLore(['§7Ignites enemies']);
```

#### setLore - Set Description

```javascript
item.setLore(loreArray);
```

```javascript
var sword = Items.create('fire_sword', 'DIAMOND_SWORD', '§cFire Sword');
sword.setLore([
    '§7Ignites enemies',
    '§cLegendary item'
]);
```

#### addAbility - Add Ability

```javascript
item.addAbility(triggerType, handler);
```

| triggerType | Description |
|-------------|----------|
| `HIT` | On hit |
| `RIGHT_CLICK` | Right click |
| `LEFT_CLICK` | Left click |

```javascript
var sword = Items.create('fire_sword', 'DIAMOND_SWORD', '§cFire Sword');
sword.setLore(['§7Ignites enemies']);

sword.addAbility('HIT', function(player, target, item) {
    target.setFireTicks(100); // Ignite for 5 seconds
    player.sendMessage('§cEnemy is burning!');
});

Items.register(sword);
```

#### register - Register Item

```javascript
Items.register(item);
```

```javascript
Items.register(sword);
```

---

## Storage - Storage

Save and load data.

### Methods

#### save - Save Data

```javascript
Storage.save(key, value);
```

```javascript
Storage.save('player.steve.coins', 100);
Storage.save('player.steve.level', 5);
```

#### load - Load Data

```javascript
Storage.load(key).then(function(value) {
    // value - loaded value
});
```

```javascript
Storage.load('player.steve.coins').then(function(coins) {
    if (coins === null) {
        coins = 0;
    }
    Console.log('Coins: ' + coins);
});
```

#### delete - Delete Data

```javascript
Storage.delete(key);
```

```javascript
Storage.delete('player.steve.temp');
```

#### exists - Check Existence

```javascript
Storage.exists(key).then(function(exists) {
    if (exists) {
        Console.log('Data exists');
    }
});
```

---

## API - Extended API

Additional methods for convenient work.

### Methods

#### registerEvent - Register Event

```javascript
API.registerEvent(eventName, handler);
```

```javascript
API.registerEvent('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    player.sendMessage('§aWelcome!');
});
```

#### broadcast - Message Everyone

```javascript
API.broadcast(message);
```

```javascript
API.broadcast('§eAnnouncement for everyone!');
```

#### getOnlinePlayers - Get Players

```javascript
var players = API.getOnlinePlayers();
```

```javascript
var count = API.getOnlinePlayers().size();
```

#### strikeLightningSync - Lightning (Safe)

```javascript
API.strikeLightningSync(location);
```

```javascript
API.strikeLightningSync(player.getLocation());
```

#### addPotionEffectSync - Potion Effect (Safe)

```javascript
API.addPotionEffectSync(player, effectType, duration, amplifier, ambient, particles);
```

```javascript
var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
API.addPotionEffectSync(player, PotionEffectType.SPEED, 999999, 2, false, false);
```

#### removePotionEffectSync - Remove Effect (Safe)

```javascript
API.removePotionEffectSync(player, effectType);
```

```javascript
var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
API.removePotionEffectSync(player, PotionEffectType.SPEED);
```

#### addAttribute - Item Attribute

```javascript
API.addAttribute(itemMeta, attributeName, modifierName, value, operation, slot);
```

| Parameter | Description |
|----------|---------|
| `itemMeta` | ItemMeta of item |
| `attributeName` | GENERIC_ATTACK_DAMAGE, GENERIC_ATTACK_SPEED, etc. |
| `modifierName` | Unique modifier name |
| `value` | Value |
| `operation` | ADD_NUMBER, MULTIPLY_SCALAR_1 |
| `slot` | HAND, FEET, LEGS, etc. |

```javascript
var Attribute = Java.type('org.bukkit.attribute.Attribute');
var AttributeModifier = Java.type('org.bukkit.attribute.AttributeModifier');
var UUID = Java.type('java.util.UUID');

API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'damage_boost', 10.0, 'ADD_NUMBER', 'HAND');
```

#### log - Logging

```javascript
API.log(message);
```

```javascript
API.log('Script loaded');
```

---

## Working with Java

ScriptsLab allows using Java classes directly.

### Import Classes

```javascript
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var Enchantment = Java.type('org.bukkit.enchantments.Enchantment');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');
var Attribute = Java.type('org.bukkit.attribute.Attribute');
var AttributeModifier = Java.type('org.bukkit.attribute.AttributeModifier');
var EquipmentSlot = Java.type('org.bukkit.inventory.EquipmentSlot');
var UUID = Java.type('java.util.UUID');
var ArrayList = Java.type('java.util.ArrayList');
var PotionEffect = Java.type('org.bukkit.potion.PotionEffect');
var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
```

### Creating Objects

```javascript
var item = new ItemStack(Material.DIAMOND_SWORD);
item.setAmount(64);

var list = new ArrayList();
list.add('element');
```

### Using Methods

```javascript
item.setAmount(64);
list.add('element');
var size = list.size();
```

### instanceof - Type Check

```javascript
var Player = Java.type('org.bukkit.entity.Player');
if (entity instanceof Player) {
    // This is a player
}
```

---

## Working with Command Sender

### Sender Types

| Method | Description |
|--------|---------|
| `sender.isPlayer()` | Check if sender is player |
| `sender.isOp()` | Check if sender is OP |

### Examples

```javascript
Commands.register('test', function(sender, args) {
    // Check if player
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    player.sendMessage('§aHello, player!');
});
```

### Getting Player from Sender

```javascript
var player = sender; // If checked with isPlayer()
```

---

## Error Handling

### try-catch

```javascript
try {
    // Your code
    var result = dangerousOperation();
} catch (error) {
    Console.error('Error: ' + error);
}
```

### Null Check

```javascript
var player = Players.get('Steve');
if (player !== null) {
    // Safe to use
} else {
    Console.warn('Player not found');
}
```

---

## Data Types

### JavaScript → Java

| JavaScript | Java |
|-----------|------|
| `number` | int, double, float |
| `string` | String |
| `boolean` | boolean |
| `array` | ArrayList, List |
| `function` | Consumer, Function |
| `object` | Object |

### Java → JavaScript

| Java | JavaScript |
|------|----------|
| `int` | number |
| `double` | number |
| `boolean` | boolean |
| `String` | string |
| `List` | array |
| `Map` | object |
| `Object` | object |

---

## Limitations

- **Timeout**: 5 seconds by default (configurable)
- **Memory**: 128MB per script (configurable)
- **Sandbox**: Limited Java API access (when sandbox: true)
- **Threads**: GraalVM JS is single-threaded

---

## Next Steps

| Step | Description |
|-----|----------|
| [Examples](examples/) | Ready-made script examples |
| [Modules](modules.md) | Module system |
| [Security](configuration.md#security) | Security setup |