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

// Импорты Java классов
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
var EntityDamageByEntityEvent = Java.type('org.bukkit.event.entity.EntityDamageByEntityEvent');
var PlayerItemHeldEvent = Java.type('org.bukkit.event.player.PlayerItemHeldEvent');

Console.log('=== Инициализация Меча Молний ===');

// Команда для получения меча
Commands.register('getlightningsword', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cЭта команда только для игроков!');
        return;
    }
    
    var player = org.bukkit.Bukkit.getPlayer(sender.getName());
    if (!player) {
        sender.sendMessage('§cОшибка: игрок не найден!');
        return;
    }
    
    // Создаём алмазный меч
    var sword = new ItemStack(Material.DIAMOND_SWORD);
    var meta = sword.getItemMeta();
    
    if (meta) {
        // Название
        meta.setDisplayName('§6§l⚡ МЕЧ МОЛНИЙ ⚡');
        
        // Описание (lore)
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
        
        // Неразрушимость
        meta.setUnbreakable(true);
        
        // Скрываем флаги (чтобы не показывать "Unbreakable" и атрибуты в описании)
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        // Добавляем зачарование для свечения (можно любое, оно будет скрыто)
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        
        // === АТРИБУТЫ через API helper ===
        // Используем Java helper метод для добавления атрибутов
        
        // +10 к урону
        API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'lightning_sword_damage', 10.0, 'ADD_NUMBER', 'HAND');
        
        // +20% к скорости атаки (+0.8)
        API.addAttribute(meta, 'GENERIC_ATTACK_SPEED', 'lightning_sword_speed', 0.8, 'ADD_NUMBER', 'HAND');
        
        Console.log('Атрибуты добавлены к мечу');
        
        sword.setItemMeta(meta);
    }
    
    // Даём меч игроку
    player.getInventory().addItem(sword);
    player.sendMessage('§6§l⚡ §aВы получили §6§lМЕЧ МОЛНИЙ§a!');
    player.sendMessage('§7Ударьте моба, чтобы увидеть его силу...');
    
    Console.log(player.getName() + ' получил Меч Молний');
}, 'scriptslab.getlightningsword');

Console.log('✓ Команда /getlightningsword зарегистрирована');

// === СПОСОБНОСТЬ 1: Молния при ударе ===
API.registerEvent('EntityDamageByEntityEvent', function(event) {
    try {
        // Проверяем, что атакующий - игрок
        var Player = Java.type('org.bukkit.entity.Player');
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        var attacker = event.getDamager();
        var victim = event.getEntity();
        var item = attacker.getInventory().getItemInMainHand();
        
        // Проверяем, что в руке наш меч
        if (!item || item.getType() !== Material.DIAMOND_SWORD) {
            return;
        }
        
        var meta = item.getItemMeta();
        if (!meta || !meta.hasDisplayName()) {
            return;
        }
        
        var displayName = meta.getDisplayName();
        if (displayName.indexOf('МЕЧ МОЛНИЙ') === -1) {
            return;
        }
        
        // ⚡ ВЫЗЫВАЕМ МОЛНИЮ! ⚡
        var location = victim.getLocation();
        API.strikeLightningSync(location);
        
        // Дополнительный урон
        event.setDamage(event.getDamage() + 5.0);
        
        // Эффекты
        attacker.sendMessage('§6⚡ §eМолния поражает врага!');
        
        Console.log(attacker.getName() + ' использовал Меч Молний');
        
    } catch (e) {
        Console.error('Ошибка в обработчике Меча Молний: ' + e);
    }
});

Console.log('✓ Способность "Молния при ударе" активирована');

// === СПОСОБНОСТЬ 2: Эффект Силы при держании ===
API.registerEvent('PlayerItemHeldEvent', function(event) {
    try {
        var player = event.getPlayer();
        var newSlot = event.getNewSlot();
        var item = player.getInventory().getItem(newSlot);
        
        // Проверяем, что взяли наш меч
        if (item && item.getType() === Material.DIAMOND_SWORD) {
            var meta = item.getItemMeta();
            if (meta && meta.hasDisplayName()) {
                var displayName = meta.getDisplayName();
                if (displayName.indexOf('МЕЧ МОЛНИЙ') !== -1) {
                    // Даём эффект Силы II на 999999 тиков (бесконечно)
                    var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
                    API.addPotionEffectSync(
                        player,
                        PotionEffectType.STRENGTH,
                        999999,
                        1, // Уровень 1 = Сила II
                        false, // ambient
                        false  // particles (скрываем частицы)
                    );
                    
                    player.sendMessage('§6⚡ §eВы чувствуете силу молнии!');
                    Console.log(player.getName() + ' получил эффект Силы от Меча Молний');
                }
            }
        }
        
        // Убираем эффект, если убрали меч
        var oldSlot = event.getPreviousSlot();
        var oldItem = player.getInventory().getItem(oldSlot);
        if (oldItem && oldItem.getType() === Material.DIAMOND_SWORD) {
            var oldMeta = oldItem.getItemMeta();
            if (oldMeta && oldMeta.hasDisplayName()) {
                var oldDisplayName = oldMeta.getDisplayName();
                if (oldDisplayName.indexOf('МЕЧ МОЛНИЙ') !== -1) {
                    var PotionEffectType = Java.type('org.bukkit.potion.PotionEffectType');
                    API.removePotionEffectSync(player, PotionEffectType.STRENGTH);
                    player.sendMessage('§7Сила молнии покидает вас...');
                }
            }
        }
        
    } catch (e) {
        Console.error('Ошибка в обработчике смены предмета: ' + e);
    }
});

Console.log('✓ Способность "Эффект Силы" активирована');

Console.log('=== Меч Молний полностью активирован! ===');

