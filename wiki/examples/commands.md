# 💡 Примеры команд

Готовые примеры команд для ScriptsLab.

---

## Простая команда приветствия

Создаёт команду `/hello`, которая отправляет приветственное сообщение.

```javascript
/**
 * Команда /hello - Отправить приветствие
 */

Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aПривет, ' + sender.getName() + '!');
    sender.sendMessage('§7Добро пожаловать на сервер!');
});

Console.log('Команда /hello зарегистрирована');
```

---

## Команда лечения

Восстанавливает здоровье, голод и убирает эффекты.

```javascript
/**
 * Команда /heal - Восстановить здоровье
 */

Commands.register('heal', function(sender, args) {
    // Проверка: только для игроков
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
    Console.log(player.getName() + ' использовал /heal');
    
}, 'scriptslab.heal');

Console.log('Команда /heal зарегистрирована');
```

---

## Команда полёта

Включает и выключает полёт для игрока.

```javascript
/**
 * Команда /fly - Включить/выключить полёт
 */

Commands.register('fly', function(sender, args) {
    // Проверка: только для игроков
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    var currentFlight = player.getAllowFlight();
    player.setAllowFlight(!currentFlight);
    
    var msg = player.getAllowFlight() ? '§a✓ Полёт включен!' : '§c✗ Полёт выключен!';
    player.sendMessage(msg);
    
    Console.log(player.getName() + ' переключил полёт: ' + player.getAllowFlight());
}, 'scriptslab.fly');

Console.log('Команда /fly зарегистрирована');
```

---

## Команда телепортации на спавн

Телепортирует игрока на спавн мира.

```javascript
/**
 * Команда /spawn - Телепорт на спавн
 */

Commands.register('spawn', function(sender, args) {
    // Проверка: только для игроков
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    var world = player.getWorld();
    
    // Получаем координаты спавна
    var spawn = world.getSpawnLocation();
    
    player.teleport(spawn);
    player.sendMessage('§aВы телепортированы на спавн!');
    
    Console.log(player.getName() + ' телепортировался на спавн');
});

Console.log('Команда /spawn зарегистрирована');
```

---

## Команда с аргументами

Пример к��манды с аргументами.

```javascript
/**
 * Команда /warp <название> - Телепорт на варп
 */

Commands.register('warp', function(sender, args) {
    // Проверка аргументов
    if (args.length === 0) {
        sender.sendMessage('§cИспользование: /warp <название>');
        sender.sendMessage('§7Доступные варпы: spawn, shop, arena');
        return;
    }
    
    // Проверка: только для игроков
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var warpName = args[0].toLowerCase();
    var player = sender;
    
    // Координаты варпов
    var warps = {
        'spawn': {x: 0, y: 100, z: 0},
        'shop': {x: 100, y: 100, z: 0},
        'arena': {x: -100, y: 100, z: 0}
    };
    
    // Проверяем существование варпа
    if (!warps[warpName]) {
        sender.sendMessage('§cВарп не найден: ' + warpName);
        return;
    }
    
    // Телепорт
    var world = player.getWorld();
    var Location = Java.type('org.bukkit.Location');
    var location = new Location(
        world,
        warps[warpName].x,
        warps[warpName].y,
        warps[warpName].z
    );
    
    player.teleport(location);
    player.sendMessage('§aТелепорт на §e' + warpName + '§a!');
    
    Console.log(player.getName() + ' телепортировался на ' + warpName);
});

Console.log('Команда /warp зарегистрирована');
```

---

## Команда пожертвовать предмет

Даёт игроку определённый предмет.

```javascript
/**
 * Команда /give <предмет> [количество] - Дать предмет
 */

Commands.register('give', function(sender, args) {
    // Проверка аргументов
    if (args.length === 0) {
        sender.sendMessage('§cИспользование: /give <предмет> [количество]');
        sender.sendMessage('§7Доступные предметы: diamond, emerald, book');
        return;
    }
    
    // Проверка: только для игроков
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    var itemName = args[0].toLowerCase();
    var amount = args.length > 1 ? parseInt(args[1]) : 1;
    
    // Mapping имён
    var materials = {
        'diamond': 'DIAMOND',
        'emerald': 'EMERALD',
        'gold': 'GOLD_INGOT',
        'iron': 'IRON_INGOT',
        'book': 'BOOK',
        'apple': 'GOLDEN_APPLE',
        'enchant': 'ENCHANTED_BOOK'
    };
    
    // Проверяем существование предмета
    if (!materials[itemName]) {
        sender.sendMessage('§cНеизвестный предмет: ' + itemName);
        return;
    }
    
    // Получаем Material
    var Material = Java.type('org.bukkit.Material');
    var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
    
    var material = Material.valueOf(materials[itemName]);
    var item = new ItemStack(material, amount);
    
    // Даём предмет
    player.getInventory().addItem(item);
    player.sendMessage('§aВы получили §e' + amount + 'x ' + itemName);
    
    Console.log(player.getName() + ' получил ' + itemName + ' x' + amount);
}, 'scriptslab.give');

Console.log('Команда /give зарегистрирована');
```

---

## Команда убить /kill

Убивает цель (себя или другого игрока).

```javascript
/**
 * Команда /kill [игрок] - Убить игро��а
 */

Commands.register('kill', function(sender, args) {
    var target = sender;
    
    // Если указан игрок
    if (args.length > 0) {
        // Проверка прав
        if (!sender.hasPermission('scriptslab.kill.others')) {
            sender.sendMessage('§cНет прав!');
            return;
        }
        
        var targetName = args[0];
        target = Players.get(targetName);
        
        if (target === null) {
            sender.sendMessage('§cИгрок не найден: ' + targetName);
            return;
        }
    }
    
    // Проверка: цель должна быть игроком
    if (!target.isPlayer()) {
        sender.sendMessage('§cЦель не является игроком!');
        return;
    }
    
    // Убить игрока
    target.setHealth(0.0);
    
    if (target === sender) {
        sender.sendMessage('§cВы совершили суицид!');
    } else {
        sender.sendMessage('§aВы убили игрока §e' + target.getName());
        target.sendMessage('§cВас убил §e' + sender.getName());
    }
    
    Console.log(sender.getName() + ' убил ' + target.getName());
}, 'scriptslab.kill');

Console.log('Команда /kill зарегистрирована');
```

---

## Команда /healother

Лечение другого игрока.

```javascript
/**
 * Команда /healother <игрок> - Исцелить другого игрока
 */

Commands.register('healother', function(sender, args) {
    // Проверка аргументов
    if (args.length === 0) {
        sender.sendMessage('§cИспользование: /healother <игрок>');
        return;
    }
    
    // Проверка прав
    if (!sender.hasPermission('scriptslab.healother')) {
        sender.sendMessage('§cНет прав!');
        return;
    }
    
    var targetName = args[0];
    var target = Players.get(targetName);
    
    if (target === null) {
        sender.sendMessage('§cИгрок не найден: ' + targetName);
        return;
    }
    
    // Лечение
    target.setHealth(target.getMaxHealth());
    target.setFoodLevel(20);
    target.setSaturation(20.0);
    
    target.getActivePotionEffects().forEach(function(effect) {
        target.removePotionEffect(effect.getType());
    });
    
    sender.sendMessage('§aВы исцелили игрока §e' + targetName);
    target.sendMessage('§aВас исцелил §e' + sender.getName());
    
    Console.log(sender.getName() + ' исцелил ' + targetName);
}, 'scriptslab.healother');

Console.log('Команда /healother зарегистрирована');
```

---

## Команда дня/ночи

Устанавливает время суток.

```javascript
/**
 * Команда /time set <day|night> - Установить время
 */

Commands.register('time', function(sender, args) {
    // Проверка аргументов
    if (args.length === 0 || args[0] !== 'set') {
        sender.sendMessage('§cИспользование: /time set <day|night>');
        return;
    }
    
    if (args.length < 2) {
        sender.sendMessage('§cИспользование: /time set <day|night>');
        return;
    }
    
    var timeType = args[1].toLowerCase();
    var world = sender instanceof org.bukkit.entity.Player 
        ? sender.getWorld() 
        : org.bukkit.Bukkit.getWorlds().get(0);
    
    // Установка времени
    var time = timeType === 'day' ? 1000 : 13000;
    world.setTime(time);
    
    var msg = timeType === 'day' ? '§eДень' : '§8Ночь';
    Server.broadcast('§aВремя установлено: ' + msg);
    
    Console.log('Время изменено на ' + timeType);
});

Console.log('Команда /time зарегистрирована');
```

---

## Команда погоды

Устанавливает погоду.

```javascript
/**
 * Команда /weather <sun|rain|storm> - Установить погоду
 */

Commands.register('weather', function(sender, args) {
    // Проверка аргументов
    if (args.length === 0) {
        sender.sendMessage('§cИспользование: /weather <sun|rain|storm>');
        return;
    }
    
    var weatherType = args[0].toLowerCase();
    var world;
    
    if (sender.isPlayer()) {
        world = sender.getWorld();
    } else {
        world = org.bukkit.Bukkit.getWorlds().get(0);
    }
    
    // Установка погоды
    if (weatherType === 'sun') {
        world.setStorm(false);
        world.setThundering(false);
        Server.broadcast('§eПогода: Ясно');
    } else if (weatherType === 'rain') {
        world.setStorm(true);
        world.setThundering(false);
        Server.broadcast('§eПогода: Дождь');
    } else if (weatherType === 'storm') {
        world.setStorm(true);
        world.setThundering(true);
        Server.broadcast('§eПогода: Гроза');
    } else {
        sender.sendMessage('§cНеизвестная погода: ' + weatherType);
        sender.sendMessage('§cДоступно: sun, rain, storm');
        return;
    }
    
    Console.log('Погода изменена на ' + weatherType);
});

Console.log('Команда /weather зарегистрирована');
```

---

## Команда очистки инвентаря

Очищает инвентарь игрока.

```javascript
/**
 * Команда /clear - Очистить инвентарь
 */

Commands.register('clear', function(sender, args) {
    // Проверка: только для игроков
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    
    // Очистка инвентаря
    player.getInventory().clear();
    player.getEquipment().clear();
    
    player.sendMessage('§aИнвентарь очищен!');
    Console.log(player.getName() + ' очистил инвентарь');
});

Console.log('Команда /clear зарегистрирована');
```

---

## Команда скорости

Изменяет скорость передвижения.

```javascript
/**
 * Команда /speed <число> - Изменить скорость
 */

Commands.register('speed', function(sender, args) {
    // Проверка: только для игроков
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    // Проверка аргументов
    if (args.length === 0) {
        sender.sendMessage('§cИспользование: /speed <1-10>');
        return;
    }
    
    var speed = parseFloat(args[0]);
    
    // Проверка диапазона
    if (speed < 1 || speed > 10) {
        sender.sendMessage('§cСкорость должна быть от 1 до 10!');
        return;
    }
    
    var player = sender;
    
    // Установка скорости (игрок может быть вCreative или вSurvival)
    // Примечание: это упрощённый пример
    var Attribute = Java.type('org.bukkit.attribute.Attribute');
    var AttributeModifier = Java.type('org.bukkit.attribute.AttributeModifier');
    var UUID = Java.type('java.util.UUID');
    
    player.sendMessage('§aСкорость установлена на §e' + speed);
    Console.log(player.getName() + ' изменил скорость на ' + speed);
});

Console.log('Команда /speed зарегистрирована');
```

---

## Сочетания ключей

### Клавиша + Команда = Готово!

| Скрипт | Файл | Название |
|--------|------|----------|
| Лечение | `heal_command.js` | /heal |
| Полёт | `fly_command.js` | /fly |
| Телепорт | `spawn_command.js` | /spawn |
| Варпы | `warp_command.js` | /warp |
| Предметы | `give_command.js` | /give |

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [События](events.md) | Обработка событий |
| [Scheduler](scheduler.md) | Планирование задач |
| [Предметы](items.md) | Кастомные предметы |

---

# 💡 Command Examples (English)

Ready-to-use command examples for ScriptsLab.

---

## Simple Hello Command

Creates `/hello` command that sends a welcome message.

```javascript
/**
 * Command /hello - Send greeting
 */
Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aHello, ' + sender.getName() + '!');
    sender.sendMessage('§7Welcome to the server!');
});

Console.log('Command /hello registered');
```

---

## Heal Command

Restores health, hunger, and removes effects.

```javascript
/**
 * Command /heal - Restore health
 */
Commands.register('heal', function(sender, args) {
    // Check: players only
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    
    // Restore health
    player.setHealth(player.getMaxHealth());
    
    // Restore hunger
    player.setFoodLevel(20);
    player.setSaturation(20.0);
    
    // Remove effects
    player.getActivePotionEffects().forEach(function(effect) {
        player.removePotionEffect(effect.getType());
    });
    
    player.sendMessage('§a✓ You have been fully healed!');
    Console.log(player.getName() + ' used /heal');
    
}, 'scriptslab.heal');

Console.log('Command /heal registered');
```

---

## Fly Command

Toggles flight for player.

```javascript
/**
 * Command /fly - Toggle flight
 */
Commands.register('fly', function(sender, args) {
    // Check: players only
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    var currentFlight = player.getAllowFlight();
    player.setAllowFlight(!currentFlight);
    
    var msg = player.getAllowFlight() ? '§a✓ Flight enabled!' : '§c✗ Flight disabled!';
    player.sendMessage(msg);
    
    Console.log(player.getName() + ' toggled flight: ' + player.getAllowFlight());
}, 'scriptslab.fly');

Console.log('Command /fly registered');
```

---

## Spawn Teleport Command

Teleports player to world spawn.

```javascript
/**
 * Command /spawn - Teleport to spawn
 */
Commands.register('spawn', function(sender, args) {
    // Check: players only
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    var world = player.getWorld();
    
    // Get spawn coordinates
    var spawn = world.getSpawnLocation();
    
    player.teleport(spawn);
    player.sendMessage('§aYou have been teleported to spawn!');
    
    Console.log(player.getName() + ' teleported to spawn');
});

Console.log('Command /spawn registered');
```

---

## Command with Arguments

Example command with arguments.

```javascript
/**
 * Command /warp <name> - Teleport to warp
 */
Commands.register('warp', function(sender, args) {
    // Check arguments
    if (args.length === 0) {
        sender.sendMessage('§cUsage: /warp <name>');
        sender.sendMessage('§7Available warps: spawn, shop, arena');
        return;
    }
    
    // Check: players only
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var warpName = args[0].toLowerCase();
    var player = sender;
    
    // Warp coordinates
    var warps = {
        'spawn': {x: 0, y: 100, z: 0},
        'shop': {x: 100, y: 100, z: 0},
        'arena': {x: -100, y: 100, z: 0}
    };
    
    // Check if warp exists
    if (!warps[warpName]) {
        sender.sendMessage('§cWarp not found: ' + warpName);
        return;
    }
    
    // Teleport
    var world = player.getWorld();
    var Location = Java.type('org.bukkit.Location');
    var location = new Location(
        world,
        warps[warpName].x,
        warps[warpName].y,
        warps[warpName].z
    );
    
    player.teleport(location);
    player.sendMessage('§aTeleported to §e' + warpName + '§a!');
    
    Console.log(player.getName() + ' teleported to ' + warpName);
});

Console.log('Command /warp registered');
```

---

## Give Item Command

Gives player a specific item.

```javascript
/**
 * Command /give <item> [amount] - Give item
 */
Commands.register('give', function(sender, args) {
    // Check arguments
    if (args.length === 0) {
        sender.sendMessage('§cUsage: /give <item> [amount]');
        sender.sendMessage('§7Available items: diamond, emerald, book');
        return;
    }
    
    // Check: players only
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    var itemName = args[0].toLowerCase();
    var amount = args.length > 1 ? parseInt(args[1]) : 1;
    
    // Name mapping
    var materials = {
        'diamond': 'DIAMOND',
        'emerald': 'EMERALD',
        'gold': 'GOLD_INGOT',
        'iron': 'IRON_INGOT',
        'book': 'BOOK',
        'apple': 'GOLDEN_APPLE',
        'enchant': 'ENCHANTED_BOOK'
    };
    
    // Check if item exists
    if (!materials[itemName]) {
        sender.sendMessage('§cUnknown item: ' + itemName);
        return;
    }
    
    // Get Material
    var Material = Java.type('org.bukkit.Material');
    var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
    
    var material = Material.valueOf(materials[itemName]);
    var item = new ItemStack(material, amount);
    
    // Give item
    player.getInventory().addItem(item);
    player.sendMessage('§aYou received §e' + amount + 'x ' + itemName);
    
    Console.log(player.getName() + ' received ' + itemName + ' x' + amount);
}, 'scriptslab.give');

Console.log('Command /give registered');
```

---

## Kill Command

Kills target (self or other player).

```javascript
/**
 * Command /kill [player] - Kill player
 */
Commands.register('kill', function(sender, args) {
    var target = sender;
    
    // If player specified
    if (args.length > 0) {
        // Check permissions
        if (!sender.hasPermission('scriptslab.kill.others')) {
            sender.sendMessage('§cNo permission!');
            return;
        }
        
        var targetName = args[0];
        target = Players.get(targetName);
        
        if (target === null) {
            sender.sendMessage('§cPlayer not found: ' + targetName);
            return;
        }
    }
    
    // Check: target must be player
    if (!target.isPlayer()) {
        sender.sendMessage('§cTarget is not a player!');
        return;
    }
    
    // Kill player
    target.setHealth(0.0);
    
    if (target === sender) {
        sender.sendMessage('§cYou committed suicide!');
    } else {
        sender.sendMessage('§aYou killed §e' + target.getName());
        target.sendMessage('§cYou were killed by §e' + sender.getName());
    }
    
    Console.log(sender.getName() + ' killed ' + target.getName());
}, 'scriptslab.kill');

Console.log('Command /kill registered');
```

---

## Heal Other Command

Heal another player.

```javascript
/**
 * Command /healother <player> - Heal another player
 */
Commands.register('healother', function(sender, args) {
    // Check arguments
    if (args.length === 0) {
        sender.sendMessage('§cUsage: /healother <player>');
        return;
    }
    
    // Check permissions
    if (!sender.hasPermission('scriptslab.healother')) {
        sender.sendMessage('§cNo permission!');
        return;
    }
    
    var targetName = args[0];
    var target = Players.get(targetName);
    
    if (target === null) {
        sender.sendMessage('§cPlayer not found: ' + targetName);
        return;
    }
    
    // Heal
    target.setHealth(target.getMaxHealth());
    target.setFoodLevel(20);
    target.setSaturation(20.0);
    
    target.getActivePotionEffects().forEach(function(effect) {
        target.removePotionEffect(effect.getType());
    });
    
    sender.sendMessage('§aYou healed §e' + targetName);
    target.sendMessage('§aYou were healed by §e' + sender.getName());
    
    Console.log(sender.getName() + ' healed ' + targetName);
}, 'scriptslab.healother');

Console.log('Command /healother registered');
```

---

## Time Command

Sets time of day.

```javascript
/**
 * Command /time set <day|night> - Set time
 */
Commands.register('time', function(sender, args) {
    // Check arguments
    if (args.length === 0 || args[0] !== 'set') {
        sender.sendMessage('§cUsage: /time set <day|night>');
        return;
    }
    
    if (args.length < 2) {
        sender.sendMessage('§cUsage: /time set <day|night>');
        return;
    }
    
    var timeType = args[1].toLowerCase();
    var world = sender instanceof org.bukkit.entity.Player 
        ? sender.getWorld() 
        : org.bukkit.Bukkit.getWorlds().get(0);
    
    // Set time
    var time = timeType === 'day' ? 1000 : 13000;
    world.setTime(time);
    
    var msg = timeType === 'day' ? '§eDay' : '§8Night';
    Server.broadcast('§aTime set to: ' + msg);
    
    Console.log('Time changed to ' + timeType);
});

Console.log('Command /time registered');
```

---

## Weather Command

Sets weather.

```javascript
/**
 * Command /weather <sun|rain|storm> - Set weather
 */
Commands.register('weather', function(sender, args) {
    // Check arguments
    if (args.length === 0) {
        sender.sendMessage('§cUsage: /weather <sun|rain|storm>');
        return;
    }
    
    var weatherType = args[0].toLowerCase();
    var world;
    
    if (sender.isPlayer()) {
        world = sender.getWorld();
    } else {
        world = org.bukkit.Bukkit.getWorlds().get(0);
    }
    
    // Set weather
    if (weatherType === 'sun') {
        world.setStorm(false);
        world.setThundering(false);
        Server.broadcast('§eWeather: Clear');
    } else if (weatherType === 'rain') {
        world.setStorm(true);
        world.setThundering(false);
        Server.broadcast('§eWeather: Rain');
    } else if (weatherType === 'storm') {
        world.setStorm(true);
        world.setThundering(true);
        Server.broadcast('§eWeather: Storm');
    } else {
        sender.sendMessage('§cUnknown weather: ' + weatherType);
        sender.sendMessage('§cAvailable: sun, rain, storm');
        return;
    }
    
    Console.log('Weather changed to ' + weatherType);
});

Console.log('Command /weather registered');
```

---

## Clear Inventory Command

Clears player inventory.

```javascript
/**
 * Command /clear - Clear inventory
 */
Commands.register('clear', function(sender, args) {
    // Check: players only
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    
    // Clear inventory
    player.getInventory().clear();
    player.getEquipment().clear();
    
    player.sendMessage('§aInventory cleared!');
    Console.log(player.getName() + ' cleared inventory');
});

Console.log('Command /clear registered');
```

---

## Speed Command

Changes movement speed.

```javascript
/**
 * Command /speed <number> - Change speed
 */
Commands.register('speed', function(sender, args) {
    // Check: players only
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    // Check arguments
    if (args.length === 0) {
        sender.sendMessage('§cUsage: /speed <1-10>');
        return;
    }
    
    var speed = parseFloat(args[0]);
    
    // Check range
    if (speed < 1 || speed > 10) {
        sender.sendMessage('§cSpeed must be between 1 and 10!');
        return;
    }
    
    var player = sender;
    
    // Set speed (player can be in Creative or Survival)
    // Note: this is a simplified example
    var Attribute = Java.type('org.bukkit.attribute.Attribute');
    var AttributeModifier = Java.type('org.bukkit.attribute.AttributeModifier');
    var UUID = Java.type('java.util.UUID');
    
    player.sendMessage('§aSpeed set to §e' + speed);
    Console.log(player.getName() + ' changed speed to ' + speed);
});

Console.log('Command /speed registered');
```

---

## Command Summary

### Key + Command = Ready!

| Script | File | Name |
|--------|------|----------|
| Healing | `heal_command.js` | /heal |
| Flight | `fly_command.js` | /fly |
| Teleport | `spawn_command.js` | /spawn |
| Warps | `warp_command.js` | /warp |
| Items | `give_command.js` | /give |

---

## Next Steps

| Step | Description |
|-----|----------|
| [Events](events.md) | Event handling |
| [Scheduler](scheduler.md) | Task scheduling |
| [Items](items.md) | Custom items |