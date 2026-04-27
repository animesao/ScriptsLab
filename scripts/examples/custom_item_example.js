/**
 * Example: Custom Item Creation
 * Demonstrates creating a custom item via JavaScript
 */

console.log("Creating custom items...");

// Create a fire sword
API.registerItem(
    "fire_sword",
    "DIAMOND_SWORD",
    "&c&lFire Sword",
    "&7Sets enemies on fire",
    "&7Damage: &c+10",
    "",
    "&6Epic Item"
);

console.log("Fire Sword registered!");

// Create a healing staff
API.registerItem(
    "healing_staff",
    "STICK",
    "&a&lHealing Staff",
    "&7Right-click to heal nearby players",
    "&7Cooldown: &e5 seconds",
    "",
    "&5Rare Item"
);

console.log("Healing Staff registered!");

// Create a speed boots
API.registerItem(
    "speed_boots",
    "LEATHER_BOOTS",
    "&b&lSpeed Boots",
    "&7Grants permanent speed boost",
    "&7Speed: &e+50%",
    "",
    "&6Legendary Item"
);

console.log("Speed Boots registered!");
console.log("All custom items created successfully!");
