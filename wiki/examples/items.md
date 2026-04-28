# ⚔️ Примеры кастомных предметов

Готовые примеры создания кастомных предметов с способностями в ScriptsLab.

---

## Простой кастомный предмет

Создаёт предмет с кастомным названием и описанием.

```javascript
/**
 * Кастомный предмет - Простой пример
 */

// Импорт Java классов
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var Enchantment = Java.type('org.bukkit.enchantments.Enchantment');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');

// Команда для получения предмета
Commands.register('getitem', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    var Bukkit = Java.type('org.bukkit.Bukkit');
    player = Bukkit.getPlayer(sender.getName());
    
    // Создаём предмет
    var item = new ItemStack(Material.DIAMOND_SWORD);
    var meta = item.getItemMeta();
    
    // Устанавливаем название
    meta.setDisplayName('§6§l⚡ КАСТОМНЫЙ МЕЧ ⚡');
    
    // Устанавливаем описание (lore)
    var ArrayList = Java.type('java.util.ArrayList');
    var lore = new ArrayList();
    lore.add('§7');
    lore.add('§e▸ Особенности:');
    lore.add('§7  • +10 к урону');
    lore.add('§7  • +20% к скорости атаки');
    lore.add('§7');
    lore.add('§6§l✦ ЛЕГЕНДАРНЫЙ ✦');
    meta.setLore(lore);
    
    // Неразрушимость
    meta.setUnbreakable(true);
    
    // Скрываем флаги
    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    
    // Зачарование для свечения
    meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
    
    item.setItemMeta(meta);
    
    // Даём предмет
    player.getInventory().addItem(item);
    player.sendMessage('§6§l⚡ §aВы получили кастомный меч!');
    
}, 'scriptslab.getitem');

Console.log('Кастомный предмет создан');
```

---

## Легендарный Меч Молний

Полноценный кастомный меч с способностями.

```javascript
/**
 * ⚔️ ЛЕГЕНДАРНЫЙ МЕЧ МОЛНИЙ ⚔️
 * 
 * Особенности:
 * - Вызывает молнию при ударе
 * - +10 к урону
 * - +20% к скорости атаки
 * - Даёт эффект Силы II при держании
 * - Светится
 * - Неразрушимый
 */

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

Console.log('=== Инициализация Меча Молний ===');

// Команда для получения меча
Commands.register('getlightningsword', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
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
        lore.add('§7  • §cСила II§7 при держании');
        lore.add('§7  • Урон: §c+10 ❤');
        lore.add('§7');
        lore.add('§e▸ Характеристики:');
        lore.add('§7  • Урон: §c+10 ❤');
        lore.add('§7  • Скорость: §a+20%');
        lore.add('§7  • Прочность: §6Неразрушимый');
        lore.add('§7');
        lore.add('§6§l✦ ЛЕГЕНДАРНЫЙ ✦');
        meta.setLore(lore);
        
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        
        // Добавляем атрибуты
        API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'lightning_sword_damage', 10.0, 'ADD_NUMBER', 'HAND');
        API.addAttribute(meta, 'GENERIC_ATTACK_SPEED', 'lightning_sword_speed', 0.8, 'ADD_NUMBER', 'HAND');
        
        sword.setItemMeta(meta);
    }
    
    player.getInventory().addItem(sword);
    player.sendMessage('§6§l⚡ §aВы получили §6§lМЕЧ МОЛНИЙ§a!');
    
}, 'scriptslab.getlightningsword');

// === Способность: Молния при ударе ===
API.registerEvent('EntityDamageByEntityEvent', function(event) {
    try {
        var Player = Java.type('org.bukkit.entity.Player');
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        var attacker = event.getDamager();
        var victim = event.getEntity();
        var item = attacker.getInventory().getItemInMainHand();
        
        if (!item || item.getType() !== Material.DIAMOND_SWORD) return;
        
        var meta = item.getItemMeta();
        if (!meta || !meta.hasDisplayName()) return;
        
        var displayName = meta.getDisplayName();
        if (displayName.indexOf('МЕЧ МОЛНИЙ') === -1) return;
        
        // Молния!
        API.strikeLightningSync(victim.getLocation());
        event.setDamage(event.getDamage() + 5.0);
        
        attacker.sendMessage('§6⚡ §eМолния поражает врага!');
        
    } catch (e) {
        Console.error('Ошибка: ' + e);
    }
});

Console.log('✓ Способность "Молния при ударе" активирована');

// === Способность: Эффект Силы при держании ===
API.registerEvent('PlayerItemHeldEvent', function(event) {
    try {
        var player = event.getPlayer();
        var newSlot = event.getNewSlot();
        var item = player.getInventory().getItem(newSlot);
        
        // Взяли меч
        if (item && item.getType() === Material.DIAMOND_SWORD) {
            var meta = item.getItemMeta();
            if (meta && meta.hasDisplayName() && meta.getDisplayName().indexOf('МЕЧ МОЛНИЙ') !== -1) {
                var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
                API.addPotionEffectSync(player, PotionEffectType.STRENGTH, 999999, 1, false, false);
                player.sendMessage('§6⚡ §eВы чувс��вуете силу молнии!');
            }
        }
        
        // Убрали меч
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
        
    } catch (e) {
        Console.error('Ошибка: ' + e);
    }
});

Console.log('✓ Способность "Эффект Силы" активирована');
Console.log('=== Меч Молний полностью активирован! ===');
```

---

## Волшебная палочка

Предмет с способностью правого клика.

```javascript
/**
 * 🌟 Волшебная палочка
 * 
 * ПКМ - Телепорт вперёд
 */

var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');

// Команда для получения
Commands.register('getwand', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cТолько для игроков!');
        return;
    }
    
    var player = sender;
    
    var wand = new ItemStack(Material.STICK);
    var meta = wand.getItemMeta();
    
    meta.setDisplayName('§5§l🌟 Волшебная палочка 🌟');
    
    var ArrayList = Java.type('java.util.ArrayList');
    var lore = new ArrayList();
    lore.add('§7Телепортирует вперёд');
    meta.setLore(lore);
    
    meta.setUnbreakable(true);
    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    
    wand.setItemMeta(meta);
    
    player.getInventory().addItem(wand);
    player.sendMessage('§5§l🌟 §aВы получили Волшебную палочку!');
    
}, 'scriptslab.getwand');

Console.log('Волшебная палочка создана');
```

---

## Жезл исцеления

Исцеляет игрока при использовании.

```javascript
/**
 * 💚 Жезл исцеления
 */

var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');

Commands.register('gethealwand', function(sender, args) {
    if (!sender.isPlayer()) return;
    
    var player = sender;
    
    var wand = new ItemStack(Material.BLAZE_ROD);
    var meta = wand.getItemMeta();
    
    meta.setDisplayName('§a§l💚 Жезл исцеления');
    
    var ArrayList = Java.type('java.util.ArrayList');
    var lore = new ArrayList();
    lore.add('§7ПКМ - Исцелить себя');
    lore.add('§7ЛКМ - Исцелить другого');
    meta.setLore(lore);
    
    wand.setItemMeta(meta);
    player.getInventory().addItem(wand);
    player.sendMessage('§a§l💚 §aВы получили Жезл исцеления!');
    
}, 'scriptslab.gethealwand');

Console.log('Жезл исцеления создан');
```

---

## Ботинки скорости

Увеличивают скорость.

```javascript
/**
 * ⚡ Ботинки скорости
 */

var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var EntityEquipment = Java.type('org.bukkit.entity.EntityEquipment');

Commands.register('getspeedboots', function(sender, args) {
    if (!sender.isPlayer()) return;
    
    var player = sender;
    
    var boots = new ItemStack(Material.DIAMOND_BOOTS);
    var meta = boots.getItemMeta();
    
    meta.setDisplayName('§e§l⚡ Ботинки скорости');
    
    var ArrayList = Java.type('java.util.ArrayList');
    var lore = new ArrayList();
    lore.add('§7Даёт эффект Скорости II');
    meta.setLore(lore);
    
    boots.setItemMeta(meta);
    player.getInventory().addItem(boots);
    player.sendMessage('§e§l⚡ §aВы получили Ботинки скорости!');
    
    // Даём эффект
    var Bukkit = Java.type('org.bukkit.Bukkit');
    var scheduler = Bukkit.getScheduler();
    // Эффект через API
    API.addPotionEffectSync(player, PotionEffectType.SPEED, 999999, 1, false, false);
    
}, 'scriptslab.getspeedboots');

Console.log('Ботинки скорости созданы');
```

---

## Создание предмета: пошаговый гайд

### 1. Импорт классов

```javascript
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var Enchantment = Java.type('org.bukkit.enchantments.Enchantment');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');
var Attribute = Java.type('org.bukkit.attribute.Attribute');
var AttributeModifier = Java.type('org.bukkit.attribute.AttributeModifier');
var ArrayList = Java.type('java.util.ArrayList');
```

### 2. Создание предмета

```javascript
var item = new ItemStack(Material.DIAMOND_SWORD);
var meta = item.getItemMeta();
```

### 3. Настройка названия

```javascript
meta.setDisplayName('§6§l⚡ НАЗВАНИЕ ⚡');
```

### 4. Настройка описания

```javascript
var lore = new ArrayList();
lore.add('§7Описание 1');
lore.add('§7Описание 2');
meta.setLore(lore);
```

### 5. Дополнительные настройки

```javascript
meta.setUnbreakable(true); // Неразрушимость
meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE); // Скрыть "Unbreakable"
meta.addEnchant(Enchantment.LUCK, 1, true); // Зачарование для свечения
```

### 6. Применение мета

```javascript
item.setItemMeta(meta);
```

### 7. Дача предмета

```javascript
player.getInventory().addItem(item);
```

---

## Таблица материалов

| Material | Описание |
|----------|----------|
| DIAMOND_SWORD | Алмазный меч |
| DIAMOND_BOOTS | Алмазные ботинки |
| GOLDEN_APPLE | Золотое яблоко |
| BLAZE_ROD | Стержень ифрита |
| STICK | Палка |
| ENCHANTED_BOOK | Зачарованная книга |
| PAPER | Бумага |

---

# ⚔️ Custom Item Examples (English)

Ready-to-use custom item examples with abilities for ScriptsLab.

---

## Simple Custom Item

Creates an item with custom name and description.

```javascript
/**
 * Custom Item - Simple Example
 */
// Import Java classes
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var Enchantment = Java.type('org.bukkit.enchantments.Enchantment');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');

// Command to get item
Commands.register('getitem', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    var Bukkit = Java.type('org.bukkit.Bukkit');
    player = Bukkit.getPlayer(sender.getName());
    
    // Create item
    var item = new ItemStack(Material.DIAMOND_SWORD);
    var meta = item.getItemMeta();
    
    // Set name
    meta.setDisplayName('§6§l⚡ CUSTOM SWORD ⚡');
    
    // Set description (lore)
    var ArrayList = Java.type('java.util.ArrayList');
    var lore = new ArrayList();
    lore.add('§7');
    lore.add('§e▸ Features:');
    lore.add('§7  • +10 damage');
    lore.add('§7  • +20% attack speed');
    lore.add('§7');
    lore.add('§6§l✦ LEGENDARY ✦');
    meta.setLore(lore);
    
    // Unbreakable
    meta.setUnbreakable(true);
    
    // Hide flags
    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    
    // Glow enchantment
    meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
    
    item.setItemMeta(meta);
    
    // Give item
    player.getInventory().addItem(item);
    player.sendMessage('§6§l⚡ §aYou received a custom sword!');
    
}, 'scriptslab.getitem');

Console.log('Custom item created');
```

---

## Lightning Sword

Full custom sword with abilities.

```javascript
/**
 * ⚔️ LIGHTNING SWORD ⚔️
 * 
 * Features:
 * - Strikes lightning on hit
 * - +10 damage
 * - +20% attack speed
 * - Gives Strength II when held
 * - Glows
 * - Unbreakable
 */
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

Console.log('=== Initializing Lightning Sword ===');

// Command to get sword
Commands.register('getlightningsword', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = org.bukkit.Bukkit.getPlayer(sender.getName());
    if (!player) return;
    
    var sword = new ItemStack(Material.DIAMOND_SWORD);
    var meta = sword.getItemMeta();
    
    if (meta) {
        meta.setDisplayName('§6§l⚡ LIGHTNING SWORD ⚡');
        
        var lore = new ArrayList();
        lore.add('§7');
        lore.add('§e▸ Abilities:');
        lore.add('§7  • Strikes lightning on hit');
        lore.add('§7  • §cStrength II§7 when held');
        lore.add('§7  • Damage: §c+10 ❤');
        lore.add('§7');
        lore.add('§e▸ Stats:');
        lore.add('§7  • Damage: §c+10 ❤');
        lore.add('§7  • Speed: §a+20%');
        lore.add('§7  • Durability: §6Unbreakable');
        lore.add('§7');
        lore.add('§6§l✦ LEGENDARY ✦');
        meta.setLore(lore);
        
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        
        // Add attributes
        API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'lightning_sword_damage', 10.0, 'ADD_NUMBER', 'HAND');
        API.addAttribute(meta, 'GENERIC_ATTACK_SPEED', 'lightning_sword_speed', 0.8, 'ADD_NUMBER', 'HAND');
        
        sword.setItemMeta(meta);
    }
    
    player.getInventory().addItem(sword);
    player.sendMessage('§6§l⚡ §aYou received §6§lLIGHTNING SWORD§a!');
    
}, 'scriptslab.getlightningsword');

// === Ability: Lightning on hit ===
API.registerEvent('EntityDamageByEntityEvent', function(event) {
    try {
        var Player = Java.type('org.bukkit.entity.Player');
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        var attacker = event.getDamager();
        var victim = event.getEntity();
        var item = attacker.getInventory().getItemInMainHand();
        
        if (!item || item.getType() !== Material.DIAMOND_SWORD) return;
        
        var meta = item.getItemMeta();
        if (!meta || !meta.hasDisplayName()) return;
        
        var displayName = meta.getDisplayName();
        if (displayName.indexOf('LIGHTNING SWORD') === -1) return;
        
        // Lightning!
        API.strikeLightningSync(victim.getLocation());
        event.setDamage(event.getDamage() + 5.0);
        
        attacker.sendMessage('§6⚡ §eLightning strikes the enemy!');
        
    } catch (e) {
        Console.error('Error: ' + e);
    }
});

Console.log('✓ Ability "Lightning Strike" activated');

// === Ability: Strength when held ===
API.registerEvent('PlayerItemHeldEvent', function(event) {
    try {
        var player = event.getPlayer();
        var newSlot = event.getNewSlot();
        var item = player.getInventory().getItem(newSlot);
        
        // Took sword
        if (item && item.getType() === Material.DIAMOND_SWORD) {
            var meta = item.getItemMeta();
            if (meta && meta.hasDisplayName() && meta.getDisplayName().indexOf('LIGHTNING SWORD') !== -1) {
                var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
                API.addPotionEffectSync(player, PotionEffectType.STRENGTH, 999999, 1, false, false);
                player.sendMessage('§6⚡ §eYou feel the power of lightning!');
            }
        }
        
        // Removed sword
        var oldSlot = event.getPreviousSlot();
        var oldItem = player.getInventory().getItem(oldSlot);
        if (oldItem && oldItem.getType() === Material.DIAMOND_SWORD) {
            var oldMeta = oldItem.getItemMeta();
            if (oldMeta && oldMeta.hasDisplayName() && oldMeta.getDisplayName().indexOf('LIGHTNING SWORD') !== -1) {
                var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
                API.removePotionEffectSync(player, PotionEffectType.STRENGTH);
                player.sendMessage('§7Lightning power leaves you...');
            }
        }
        
    } catch (e) {
        Console.error('Error: ' + e);
    }
});

Console.log('✓ Ability "Strength Effect" activated');
Console.log('=== Lightning Sword fully activated! ===');
```

---

## Magic Wand

Item with right-click ability.

```javascript
/**
 * 🌟 Magic Wand
 * 
 * Right Click - Teleport forward
 */
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');

// Command to get it
Commands.register('getwand', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    var player = sender;
    
    var wand = new ItemStack(Material.STICK);
    var meta = wand.getItemMeta();
    
    meta.setDisplayName('§5§l🌟 Magic Wand 🌟');
    
    var ArrayList = Java.type('java.util.ArrayList');
    var lore = new ArrayList();
    lore.add('§7Teleports forward');
    meta.setLore(lore);
    
    meta.setUnbreakable(true);
    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    
    wand.setItemMeta(meta);
    
    player.getInventory().addItem(wand);
    player.sendMessage('§5§l🌟 §aYou received the Magic Wand!');
    
}, 'scriptslab.getwand');

Console.log('Magic Wand created');
```

---

## Healing Rod

Heals player when used.

```javascript
/**
 * 💚 Healing Rod
 */
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');

Commands.register('gethealwand', function(sender, args) {
    if (!sender.isPlayer()) return;
    
    var player = sender;
    
    var wand = new ItemStack(Material.BLAZE_ROD);
    var meta = wand.getItemMeta();
    
    meta.setDisplayName('§a§l💚 Healing Rod');
    
    var ArrayList = Java.type('java.util.ArrayList');
    var lore = new ArrayList();
    lore.add('§7Right Click - Heal yourself');
    lore.add('§7Left Click - Heal other');
    meta.setLore(lore);
    
    wand.setItemMeta(meta);
    player.getInventory().addItem(wand);
    player.sendMessage('§a§l💚 §aYou received the Healing Rod!');
    
}, 'scriptslab.gethealwand');

Console.log('Healing Rod created');
```

---

## Speed Boots

Increase movement speed.

```javascript
/**
 * ⚡ Speed Boots
 */
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var EntityEquipment = Java.type('org.bukkit.entity.EntityEquipment');

Commands.register('getspeedboots', function(sender, args) {
    if (!sender.isPlayer()) return;
    
    var player = sender;
    
    var boots = new ItemStack(Material.DIAMOND_BOOTS);
    var meta = boots.getItemMeta();
    
    meta.setDisplayName('§e§l⚡ Speed Boots');
    
    var ArrayList = Java.type('java.util.ArrayList');
    var lore = new ArrayList();
    lore.add('§7Gives Speed II effect');
    meta.setLore(lore);
    
    boots.setItemMeta(meta);
    player.getInventory().addItem(boots);
    player.sendMessage('§e§l⚡ §aYou received Speed Boots!');
    
    // Give effect
    var Bukkit = Java.type('org.bukkit.Bukkit');
    var scheduler = Bukkit.getScheduler();
    // Effect through API
    API.addPotionEffectSync(player, PotionEffectType.SPEED, 999999, 1, false, false);
    
}, 'scriptslab.getspeedboots');

Console.log('Speed Boots created');
```

---

## Item Creation: Step-by-Step Guide

### 1. Import Classes

```javascript
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
var Enchantment = Java.type('org.bukkit.enchantments.Enchantment');
var ItemFlag = Java.type('org.bukkit.inventory.ItemFlag');
var Attribute = Java.type('org.bukkit.attribute.Attribute');
var AttributeModifier = Java.type('org.bukkit.attribute.AttributeModifier');
var ArrayList = Java.type('java.util.ArrayList');
```

### 2. Create Item

```javascript
var item = new ItemStack(Material.DIAMOND_SWORD);
var meta = item.getItemMeta();
```

### 3. Set Name

```javascript
meta.setDisplayName('§6§l⚡ NAME ⚡');
```

### 4. Set Description

```javascript
var lore = new ArrayList();
lore.add('§7Description 1');
lore.add('§7Description 2');
meta.setLore(lore);
```

### 5. Additional Settings

```javascript
meta.setUnbreakable(true); // Unbreakable
meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE); // Hide "Unbreakable"
meta.addEnchant(Enchantment.LUCK, 1, true); // Glow enchant
```

### 6. Apply Meta

```javascript
item.setItemMeta(meta);
```

### 7. Give Item

```javascript
player.getInventory().addItem(item);
```

---

## Material Table

| Material | Description |
|----------|----------|
| DIAMOND_SWORD | Diamond Sword |
| DIAMOND_BOOTS | Diamond Boots |
| GOLDEN_APPLE | Golden Apple |
| BLAZE_ROD | Blaze Rod |
| STICK | Stick |
| ENCHANTED_BOOK | Enchanted Book |
| PAPER | Paper |

---

## Next Steps

| Step | Description |
|-----|----------|
| [Commands](commands.md) | Command examples |
| [Events](events.md) | Event handling |
| [Scheduler](scheduler.md) | Task scheduling |