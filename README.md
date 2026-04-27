# ⚡ ScriptsLab

<div align="center">

![ScriptsLab Logo](https://img.shields.io/badge/ScriptsLab-v1.0.0-blue?style=for-the-badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![Paper](https://img.shields.io/badge/Paper-1.21.8-green?style=for-the-badge)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge)](https://adoptium.net/)

**Production-Grade JavaScript Scripting Framework for Minecraft Paper Servers**

[Features](#-features) • [Installation](#-installation) • [Quick Start](#-quick-start) • [Documentation](#-documentation) • [Examples](#-examples) • [Contributing](#-contributing)

</div>

---

## 📖 Overview

ScriptsLab is a powerful, production-ready plugin framework for Paper/Spigot servers that enables server administrators and developers to create custom gameplay features using JavaScript. Built on **GraalVM**, it provides a clean, modern API with full access to the Bukkit/Paper API.

### Why ScriptsLab?

- 🚀 **Zero Restart Required** - Hot-reload scripts without restarting your server
- 🎯 **Clean Architecture** - Modular design with dependency injection
- ⚡ **High Performance** - Powered by GraalVM JavaScript engine
- 🔒 **Thread-Safe** - Automatic synchronization for Bukkit API calls
- 🎨 **Rich API** - Commands, events, items, storage, scheduler, and more
- 📦 **Module System** - Organize code into reusable modules
- 🛠️ **Developer Friendly** - Modern JavaScript with full IDE support

---

## ✨ Features

### Core Features
- **🎮 Command System** - Register custom commands with permissions
- **📡 Event System** - Listen to any Bukkit/Paper event
- **⚔️ Custom Items** - Create items with custom abilities and attributes
- **💾 Storage System** - YAML-based persistent data storage
- **⏰ Task Scheduler** - Sync/async task scheduling
- **📊 Metrics Collection** - Built-in performance monitoring
- **🔌 Module System** - Hot-loadable plugin modules

### Advanced Features
- **Thread-Safe API** - Automatic main-thread scheduling for Bukkit calls
- **Dependency Injection** - Clean service architecture
- **Event Bus** - Internal plugin communication
- **Script Hot-Reload** - Update scripts without restart
- **Unrestricted Mode** - Full Java API access (configurable)

---

## 📦 Installation

### Requirements
- **Minecraft Server**: Paper 1.21.8+ (or compatible Spigot fork)
- **Java**: 17 or higher
- **Memory**: Minimum 2GB RAM (GraalVM included in JAR)

### Steps

1. **Download** the latest `ScriptsLab-1.0.0.jar` from [Releases](https://github.com/yourusername/ScriptsLab/releases)

2. **Install** the plugin:
   ```bash
   # Place JAR in your server's plugins folder
   cp ScriptsLab-1.0.0.jar /path/to/server/plugins/
   ```

3. **Start** your server:
   ```bash
   java -Xmx2G -jar paper.jar
   ```

4. **Verify** installation:
   ```
   [ScriptsLab] ✓ Script Engine initialized
   [ScriptsLab] ✓ Loaded X scripts
   ```

---

## 🚀 Quick Start

### Your First Script

Create a file `plugins/ScriptsLab/scripts/hello.js`:

```javascript
// Register a simple command
Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aHello, ' + sender.getName() + '!');
}, 'scriptslab.hello');

Console.log('Hello command registered!');
```

**That's it!** The script loads automatically. Use `/hello` in-game.

### Example: Fly Command

```javascript
Commands.register('fly', function(sender, args) {
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly players can fly!');
        return;
    }
    
    var flying = !sender.getAllowFlight();
    sender.setAllowFlight(flying);
    
    if (flying) {
        sender.sendMessage('§aFlight enabled!');
    } else {
        sender.sendMessage('§cFlight disabled!');
    }
}, 'scriptslab.fly');
```

### Example: Welcome Message

```javascript
API.registerEvent('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    var Bukkit = Java.type('org.bukkit.Bukkit');
    
    // Remove vanilla message
    event.joinMessage(null);
    
    // Send custom welcome
    player.sendMessage('§6§l⚡ Welcome to the server!');
    player.sendMessage('§7Online: §a' + Bukkit.getOnlinePlayers().size());
});
```

---

## 📚 Documentation

### API Reference

#### Commands API
```javascript
// Register command
Commands.register(name, handler, permission);

// Example
Commands.register('heal', function(sender, args) {
    sender.setHealth(sender.getMaxHealth());
    sender.sendMessage('§aHealed!');
}, 'scriptslab.heal');
```

#### Events API
```javascript
// Register event handler
API.registerEvent(eventName, handler);

// Example
API.registerEvent('PlayerDeathEvent', function(event) {
    var player = event.getPlayer();
    Console.log(player.getName() + ' died!');
});
```

#### Items API
```javascript
// Create custom item
Items.registerItem(id, material, displayName, ...lore);

// Give item to player
Items.giveItem(player, itemId, amount);

// Add attributes (thread-safe)
API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'modifier_name', 10.0, 'ADD_NUMBER', 'HAND');
```

#### Scheduler API
```javascript
// Run task later (ticks)
Scheduler.runLater(function() {
    Console.log('Delayed task!');
}, 20); // 1 second

// Run repeating task
Scheduler.runTimer(function() {
    Console.log('Every second!');
}, 0, 20); // delay, period

// Run async task
Scheduler.runAsync(function() {
    // Heavy computation
});
```

#### Storage API
```javascript
// Get repository
var repo = Storage.getRepository('mydata');

// Save data
repo.set('player.uuid', playerData);
repo.save();

// Load data
var data = repo.get('player.uuid');
```

#### Thread-Safe Bukkit API
```javascript
// These methods automatically run on main thread
API.addPotionEffectSync(player, effectType, duration, amplifier, ambient, particles);
API.strikeLightningSync(location);
API.removePotionEffectSync(player, effectType);
```

#### Console API
```javascript
Console.log('Info message');
Console.warn('Warning message');
Console.error('Error message');
```

---

## 🎯 Examples

### Lightning Sword

Complete example of a custom weapon with abilities:

```javascript
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');

// Command to get the sword
Commands.register('getlightningsword', function(sender, args) {
    if (!sender.isPlayer()) return;
    
    var player = org.bukkit.Bukkit.getPlayer(sender.getName());
    var sword = new ItemStack(Material.DIAMOND_SWORD);
    var meta = sword.getItemMeta();
    
    meta.setDisplayName('§6§l⚡ LIGHTNING SWORD ⚡');
    meta.setUnbreakable(true);
    
    // Add attributes
    API.addAttribute(meta, 'GENERIC_ATTACK_DAMAGE', 'lightning_damage', 10.0, 'ADD_NUMBER', 'HAND');
    API.addAttribute(meta, 'GENERIC_ATTACK_SPEED', 'lightning_speed', 0.8, 'ADD_NUMBER', 'HAND');
    
    sword.setItemMeta(meta);
    player.getInventory().addItem(sword);
    player.sendMessage('§6⚡ You received the Lightning Sword!');
});

// Strike lightning on hit
API.registerEvent('EntityDamageByEntityEvent', function(event) {
    var Player = Java.type('org.bukkit.entity.Player');
    if (!(event.getDamager() instanceof Player)) return;
    
    var attacker = event.getDamager();
    var item = attacker.getInventory().getItemInMainHand();
    
    if (item && item.getType() === Material.DIAMOND_SWORD) {
        var meta = item.getItemMeta();
        if (meta && meta.hasDisplayName() && 
            meta.getDisplayName().indexOf('LIGHTNING SWORD') !== -1) {
            
            // Strike lightning (thread-safe)
            var location = event.getEntity().getLocation();
            API.strikeLightningSync(location);
            
            event.setDamage(event.getDamage() + 5.0);
            attacker.sendMessage('§6⚡ Lightning strikes!');
        }
    }
});
```

More examples in the [`scripts/`](scripts/) directory!

---

## 🏗️ Architecture

ScriptsLab follows **Clean Architecture** principles:

```
┌─────────────────────────────────────┐
│         JavaScript Scripts          │
│    (User-defined functionality)     │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│          Script API Layer           │
│   (ScriptAPIImpl, Thread-Safety)    │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│         Core Services Layer         │
│  (Commands, Events, Items, etc.)    │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│      Bukkit/Paper API Layer         │
│    (Minecraft Server Platform)      │
└─────────────────────────────────────┘
```

### Key Components

- **GraalScriptEngine** - GraalVM JavaScript execution
- **CommandManager** - Dynamic command registration
- **EventBus** - Event handling and distribution
- **ItemManager** - Custom item management
- **StorageManager** - Persistent data storage
- **TaskScheduler** - Async/sync task scheduling
- **DI Container** - Dependency injection

---

## 🔧 Building from Source

### Prerequisites
- Java 17 JDK
- Maven 3.8+
- Git

### Build Steps

```bash
# Clone repository
git clone https://github.com/yourusername/ScriptsLab.git
cd ScriptsLab

# Build with Maven
mvn clean package -DskipTests

# Output: target/ScriptsLab-1.0.0.jar (~50MB with GraalVM)
```

### Development

```bash
# Run tests
mvn test

# Generate documentation
mvn javadoc:javadoc

# Clean build
mvn clean
```

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Contribution Guidelines

- Follow existing code style
- Add tests for new features
- Update documentation
- Keep commits atomic and descriptive

---

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **GraalVM Team** - For the amazing JavaScript engine
- **Paper Team** - For the excellent Minecraft server platform
- **Bukkit/Spigot Community** - For the plugin API foundation

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/ScriptsLab/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/ScriptsLab/discussions)
- **Wiki**: [Documentation Wiki](https://github.com/yourusername/ScriptsLab/wiki)

---

<div align="center">

**Made with ❤️ for the Minecraft community**

⭐ Star us on GitHub if you find this useful!

</div>
#   S c r i p t s L a b  
 