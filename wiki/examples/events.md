# 🎯 Примеры обработки событий

Готовые примеры обработки событий в ScriptsLab.

---

## Событие входа игрока

Срабатывает при входе игрока на сервер.

```javascript
/**
 * PlayerJoinEvent - Вход игрока на сервер
 */

Events.on('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    
    // Убираем ванильное сообщение
    event.joinMessage(null);
    
    // Отправляем кастомное приветствие
    var Bukkit = Java.type('org.bukkit.Bukkit');
    
    player.sendMessage('§8§m                                           ');
    player.sendMessage('');
    player.sendMessage('  §6§l⚡ Добро пожаловать! ⚡');
    player.sendMessage('');
    player.sendMessage('  §7Привет, §e' + player.getName() + '§7!');
    player.sendMessage('  §7Онлайн: §a' + Bukkit.getOnlinePlayers().size() + ' §7игроков');
    player.sendMessage('');
    player.sendMessage('  §7Используй §e/help §7для помощи');
    player.sendMessage('');
    player.sendMessage('§8§m                                           ');
    
    // Сообщение другим игрокам
    Bukkit.getOnlinePlayers().forEach(function(p) {
        if (p.getName() !== player.getName()) {
            p.sendMessage('§8[§a+§8] §7' + player.getName() + ' §aзашёл на сервер');
        }
    });
    
    Console.log(player.getName() + ' зашёл на сервер');
});

Console.log('Обработчик PlayerJoinEvent загружен');
```

---

## Событие выхода игрока

Срабатывает при выходе игрока с сервера.

```javascript
/**
 * PlayerQuitEvent - Выход игрока с сервера
 */

Events.on('PlayerQuitEvent', function(event) {
    var player = event.getPlayer();
    
    // Убираем ванильное сообщение
    event.quitMessage(null);
    
    // Кастомное сообщение
    var Bukkit = Java.type('org.bukkit.Bukkit');
    Bukkit.getOnlinePlayers().forEach(function(p) {
        if (p.getName() !== player.getName()) {
            p.sendMessage('§8[§c-§8] §7' + player.getName() + ' §cвышел с сервера');
        }
    });
    
    Console.log(player.getName() + ' вышел с сервера');
});

Console.log('Обработчик PlayerQuitEvent загружен');
```

---

## Событие смерти

Срабатывает при смерти игрока.

```javascript
/**
 * PlayerDeathEvent - Смерть игрока
 */

Events.on('PlayerDeathEvent', function(event) {
    var entity = event.getEntity();
    var killer = event.getEntity().getKiller();
    
    // Проверяем, был ли убит игрок
    if (killer !== null) {
        var message = '§c' + entity.getName() + ' §7был убит §c' + killer.getName();
        event.deathMessage(message);
        
        Console.log(entity.getName() + ' был убит ' + killer.getName());
    } else {
        var message = '§c' + entity.getName() + ' §7умер';
        event.deathMessage(message);
        
        Console.log(entity.getName() + ' умер');
    }
});

Console.log('Обработчик PlayerDeathEvent загружен');
```

---

## Событие чата

Срабатывает при отправке сообщения в чат.

```javascript
/**
 * AsyncPlayerChatEvent - Сообщение в чат
 */

Events.on('AsyncPlayerChatEvent', function(event) {
    var player = event.getPlayer();
    var message = event.getMessage();
    
    // Фильтр плохих слов
    var badWords = ['badword1', 'badword2', 'мат'];
    
    for (var i = 0; i < badWords.length; i++) {
        if (message.toLowerCase().indexOf(badWords[i]) !== -1) {
            event.setCancelled(true);
            player.sendMessage('§cПожалуйста, следите за речью!');
            player.sendMessage('§7Нарушение: запрещённое слово');
            Console.log(player.getName() + '使用了 плохое слово: ' + badWords[i]);
            return;
        }
    }
    
    // Добавляем префикс к сообщению
    var format = '§e' + player.getName() + ' §7» §f' + message;
    event.setFormat(format);
});

Console.log('Обработчик AsyncPlayerChatEvent загружен');
```

---

## Событие урона

Срабатывает при получении урона.

```javascript
/**
 * EntityDamageEvent - Получение урона
 */

Events.on('EntityDamageEvent', function(event) {
    var entity = event.getEntity();
    var damage = event.getDamage();
    
    // Логирование
    Console.log(entity.getName() + ' получил урон: ' + damage);
    
    // Пример: нельзя умереть от падения с малой высоты
    var cause = event.getCause();
    if (cause.toString() === 'FALL') {
        if (damage < 5) {
            event.setCancelled(true);
        }
    }
});

Console.log('Обработчик EntityDamageEvent загружен');
```

---

## Событие атаки

Срабатывает при атаке одного моба другим.

```javascript
/**
 * EntityDamageByEntityEvent - Атака сущности
 */

Events.on('EntityDamageByEntityEvent', function(event) {
    var damager = event.getDamager();
    var victim = event.getEntity();
    
    var Player = Java.type('org.bukkit.entity.Player');
    
    // Проверяем, что атакующий - игрок
    if (damager instanceof Player) {
        Console.log(damager.getName() + ' атаковал ' + victim.getName());
    }
});

Console.log('Обработчик EntityDamageByEntityEvent загружен');
```

---

## Событие стрельбы из лука

```javascript
/**
 * EntityShootBowEvent - Стрельба из лука
 */

Events.on('EntityShootBowEvent', function(event) {
    var entity = event.getEntity();
    var item = event.getBow();
    
    Console.log(entity.getName() + ' стреляет из лука');
    
    // Проверяем, что это игрок
    var Player = Java.type('org.bukkit.entity.Player');
    if (entity instanceof Player) {
        // Дополнительная логика
    }
});

Console.log('Обработчик EntityShootBowEvent загружен');
```

---

## Событие установки блока

```javascript
/**
 * BlockPlaceEvent - Установка блока
 */

Events.on('BlockPlaceEvent', function(event) {
    var player = event.getPlayer();
    var block = event.getBlock();
    var type = block.getType();
    
    // Запрет установки определённых блоков
    var blockedBlocks = ['TNT', 'LAVA', 'WATER'];
    
    for (var i = 0; i < blockedBlocks.length; i++) {
        if (type.toString() === blockedBlocks[i]) {
            event.setCancelled(true);
            player.sendMessage('§cНельзя ставить: ' + type);
            Console.log(player.getName() + ' попытался поставить ' + type);
            return;
        }
    }
    
    Console.log(player.getName() + ' поставил ' + type);
});

Console.log('Обработчик BlockPlaceEvent загружен');
```

---

## Событие разрушения блока

```javascript
/**
 * BlockBreakEvent - Разрушение блока
 */

Events.on('BlockBreakEvent', function(event) {
    var player = event.getPlayer();
    var block = event.getBlock();
    var type = block.getType();
    
    // Запрет разрушения определённых блоков
    var protectedBlocks = ['BEDROCK', 'COMMAND_BLOCK'];
    
    for (var i = 0; i < protectedBlocks.length; i++) {
        if (type.toString() === protectedBlocks[i]) {
            event.setCancelled(true);
            player.sendMessage('§cНельзя ломать: ' + type);
            Console.log(player.getName() + ' попытался сломать ' + type);
            return;
        }
    }
    
    Console.log(player.getName() + ' сломал ' + type);
});

Console.log('Обработчик BlockBreakEvent загружен');
```

---

## Событие входа в регион (WorldGuard)

```javascript
/**
 * PlayerMoveEvent - Движение игрока
 * Можно использовать для проверки входа в регион
 */

Events.on('PlayerMoveEvent', function(event) {
    var player = event.getPlayer();
    var from = event.getFrom();
    var to = event.getTo();
    
    // Проверяем, изменилась ли позиция
    if (from.getBlockX() !== to.getBlockX() || 
        from.getBlockY() !== to.getBlockY() || 
        from.getBlockZ() !== to.getBlockZ()) {
        
        // Логика проверки региона
        // WorldGuard API: event.getPlayer().inWorldRegion("region_name")
    }
});

Console.log('Обработчик PlayerMoveEvent загружен');
```

---

## Событие изменения предмета в руке

```javascript
/**
 * PlayerItemHeldEvent - Смена предмета в руке
 */

Events.on('PlayerItemHeldEvent', function(event) {
    var player = event.getPlayer();
    var newSlot = event.getNewSlot();
    var oldSlot = event.getPreviousSlot();
    
    var newItem = player.getInventory().getItem(newSlot);
    var oldItem = player.getInventory().getItem(oldSlot);
    
    Console.log(player.getName() + ' сменил предмет: ' + oldSlot + ' -> ' + newSlot);
    
    // Пример: проверка кастомного предмета
    if (newItem !== null && newItem.hasItemMeta()) {
        var meta = newItem.getItemMeta();
        if (meta.hasDisplayName() && meta.getDisplayName().indexOf('МЕЧ') !== -1) {
            player.sendMessage('§6Вы взяли меч!');
        }
    }
});

Console.log('Обработчик PlayerItemHeldEvent загружен');
```

---

## Таблица событий

| Событие | Описание | Пример файла |
|---------|----------|--------------|
| PlayerJoinEvent | Вход | welcome_message.js |
| PlayerQuitEvent | Выход | welcome_message.js |
| AsyncPlayerChatEvent | Чат | event_listener.js |
| PlayerDeathEvent | Смерть | - |
| EntityDamageByEntityEvent | Атака | custom_sword.js |
| PlayerItemHeldEvent | Смена предмета | custom_sword.js |
| BlockPlaceEvent | Установка блока | - |
| BlockBreakEvent | Разрушение блока | - |

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Команды](commands.md) | Примеры команд |
| [Scheduler](scheduler.md) | Планирование задач |
| [Предметы](items.md) | Кастомные предметы |