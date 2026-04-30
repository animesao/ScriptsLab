# 📜 Готовые скрипты

Полностью готовые к использованию скрипты для ScriptsLab.

---

## Приветственное сообщение

Полная система приветствия и прощания игроков.

**Файл**: `plugins/ScriptsLab/scripts/welcome_message.js`

```javascript
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
```

---

## Система лечения

Команда `/heal` для восстановления здоровья.

**Файл**: `plugins/ScriptsLab/scripts/heal_command.js`

```javascript
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
```

---

## Система полёта

Команда `/fly` для включения полёта.

**Файл**: `plugins/ScriptsLab/scripts/fly_command.js`

```javascript
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
```

---

## Автоматические объявления

Регулярные объявления для игроков.

**Файл**: `plugins/ScriptsLab/scripts/auto_broadcast.js`

```javascript
/**
 * Автоматические объявления
 */

var messages = [
    '§6[Объявление] §eНе забудьте проголосовать за сервер!',
    '§6[Объявление] §eПосетите наш Discord!',
    '§6[Объявление] §eИспользуйте §a/fly §eдля полёта!',
    '§6[Объявление] §eИспользуйте §a/heal §eдля лечения!'
];

var currentIndex = 0;

// Отправлять сообщение каждые 5 минут
Scheduler.runTimer(function() {
    Server.broadcast(messages[currentIndex]);
    currentIndex = (currentIndex + 1) % messages.length;
}, 100, 6000); // 100 тиков задержка, 6000 тиков = 5 минут

Console.log('Автоматические объявления запущены');
```

---

## Легендарный Меч Молний

Сложный кастомный предмет с несколькими способностями.

**Файл**: `plugins/ScriptsLab/scripts/custom_sword.js`

```javascript
/**
 * ⚔️ ЛЕГЕНДАРНЫЙ МЕЧ МОЛНИЙ ⚔️
 * 
 * Особенности:
 * - Вызывает молнию при ударе
 * - +10 к урону (атрибут)
 * - +20% к скорости атаки
 * - Даёт эффект Силы II при держании
 * - Светится (enchant glow)
 * - Неразрушимый
 */

var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var Enchantment = Java.type('org.bukkit.enchantments.Enchantment');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');
var ArrayList = Java.type('java.util.ArrayList');

Console.log('=== Инициализация Меча Молний ===');

// Команда для получения меча
Commands.register('getlightningsword', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cЭта команда только для игроков!');
        return;
    }
    
    var player = org.bukkit.Bukkit.getPlayer(sender.getName());
    if (!player) return;
    
    var sword = new ItemStack(Material.DIAMOND_SWORD);
    var meta = sword.getItemMeta();
    
    if (meta) {
        meta.setDisplayName('§6§l⚡ МЕЧ МОЛНИЙ ⚡');
        
        var lore = new ArrayList();
        lore.add('§7');
        lore.add('§e▸ Способности:');
        lore.add('§7  • Вызывает молнию при ударе');
        lore.add('§7  • Даёт эффект §cСилы II§7 при держании');
        lore.add('§7  • Дополнительный урон: §c+5 ❤');
        lore.add('§7');
        lore.add('§e▸ Характеристики:');
        lore.add('§7  • Урон: §c+10 ❤');
        lore.add('§7  • Скорость атаки: §a+20%');
        lore.add('§7  • Прочность: §6Неразрушимый');
        lore.add('§7');
        lore.add('§6§l✦ ЛЕГЕНДАРНЫЙ ПРЕДМЕТ ✦');
        meta.setLore(lore);
        
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        
        API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'lightning_sword_damage', 10.0, 'ADD_NUMBER', 'HAND');
        API.addAttribute(meta, 'GENERIC_ATTACK_SPEED', 'lightning_sword_speed', 0.8, 'ADD_NUMBER', 'HAND');
        
        sword.setItemMeta(meta);
    }
    
    player.getInventory().addItem(sword);
    player.sendMessage('§6§l⚡ §aВы получили §6§lМЕЧ МОЛНИЙ§a!');
    player.sendMessage('§7Ударьте моба, чтобы увидеть его силу...');
    
}, 'scriptslab.getlightningsword');

Console.log('✓ Команда /getlightningsword зарегистрирована');

// === СПОСОБНОСТЬ 1: Молния при ударе ===
API.registerEvent('EntityDamageByEntityEvent', function(event) {
    var Player = Java.type('org.bukkit.entity.Player');
    if (!(event.getDamager() instanceof Player)) return;
    
    var attacker = event.getDamager();
    var victim = event.getEntity();
    var item = attacker.getInventory().getItemInMainHand();
    
    if (!item || item.getType() !== Material.DIAMOND_SWORD) return;
    
    var meta = item.getItemMeta();
    if (!meta || !meta.hasDisplayName()) return;
    
    var displayName = meta.getDisplayName();
    if (displayName.indexOf('МЕЧ МОЛНИЙ') === -1) return;
    
    API.strikeLightningSync(victim.getLocation());
    event.setDamage(event.getDamage() + 5.0);
    attacker.sendMessage('§6⚡ §eМолния поражает врага!');
    
    Console.log(attacker.getName() + ' использовал Меч Молний');
});

Console.log('✓ Способность "Молния при ударе" активирована');

// === СПОСОБНОСТЬ 2: Эффект Силы при держании ===
API.registerEvent('PlayerItemHeldEvent', function(event) {
    var player = event.getPlayer();
    var newSlot = event.getNewSlot();
    var item = player.getInventory().getItem(newSlot);
    
    if (item && item.getType() === Material.DIAMOND_SWORD) {
        var meta = item.getItemMeta();
        if (meta && meta.hasDisplayName() && meta.getDisplayName().indexOf('МЕЧ МОЛНИЙ') !== -1) {
            var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
            API.addPotionEffectSync(player, PotionEffectType.STRENGTH, 999999, 1, false, false);
            player.sendMessage('§6⚡ §eВы чувствуете силу молнии!');
        }
    }
    
    var oldSlot = event.getPreviousSlot();
    var oldItem = player.getInventory().getItem(oldSlot);
    if (oldItem && oldItem.getType() === Material.DIAMOND_SWORD) {
        var oldMeta = oldItem.getItemMeta();
        if (oldMeta && oldMeta.hasDisplayName() && oldMeta.getDisplayName().indexOf('МЕЧ МОЛНИЙ') !== -1) {
            var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
            API.removePotionEffectSync(player, PotionEffectType.STRENGTH);
            player.sendMessage('§7Сила молнии покидает вас...');
        }
    }
});

Console.log('✓ Способность "Эффект Силы" активирована');
Console.log('=== Меч Молний полностью активирован! ===');
```

---

## Быстрая установка скриптов

### Шаг 1: Создайте папку скриптов

Создайте папку `plugins/ScriptsLab/scripts/`, если её нет.

### Шаг 2: Скопируйте скрипт

Скопируйте код скрипта в новый файл `.js` в папке скриптов.

### Шаг 3: Перезагрузите

Выполните `/script reload` для загрузки скрипта.

---

## Таблица готовых скриптов

| Скрипт | Файл | Команда | Описание |
|--------|------|---------|-----------|
| Приветствие | welcome_message.js | Автоматически | Вход/выход |
| Лечение | heal_command.js | /heal | Восстановить HP |
| Полёт | fly_command.js | /fly | Включить полёт |
| Объявления | auto_broadcast.js | Автоматически | Объявления |
| Меч | custom_sword.js | /getlightningsword | Легендарный меч |

---

## Дальнейшие шаги

| Раздел | Описание |
|--------|-----------|
| [Script API](../script-api.md) | Полный API |
| [Модули](../modules.md) | Система модулей |
| [Тroubleshooting](../troubleshooting.md) | Решение проблем |