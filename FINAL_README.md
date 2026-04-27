# FrameworkPlugin - Production-Grade Modular Framework

## 🎯 Overview

FrameworkPlugin is a **production-ready**, **framework-level** plugin system for Minecraft Paper 1.21.8. It's not just a plugin—it's a **mini game engine** inside Minecraft.

### Key Features

✅ **Modular Architecture** - Clean Architecture with SOLID principles  
✅ **Dependency Injection** - Lightweight custom DI container  
✅ **JavaScript Support** - GraalVM with ECMAScript 2022  
✅ **Custom Event Bus** - Cross-module communication  
✅ **Item System** - Custom items with abilities and NBT  
✅ **Task Scheduler** - CompletableFuture-based async operations  
✅ **Hot Reload** - Reload modules and scripts without restart  
✅ **Thread-Safe** - Concurrent collections and atomic operations  
✅ **Zero Hardcoded Logic** - Everything configurable or scriptable  

## 📁 Project Structure

```
FrameworkPlugin/
├── src/main/java/com/myplugin/
│   ├── FrameworkPlugin.java           # Main plugin class
│   │
│   ├── api/                            # Public interfaces (API layer)
│   │   ├── module/                     # Module system API
│   │   ├── item/                       # Item system API
│   │   ├── event/                      # Event bus API
│   │   ├── scheduler/                  # Scheduler API
│   │   ├── script/                     # Script engine API
│   │   └── storage/                    # Storage API
│   │
│   ├── core/                           # Implementation layer
│   │   ├── di/                         # Dependency injection
│   │   ├── module/                     # Module management
│   │   ├── item/                       # Item management
│   │   ├── event/                      # Event bus implementation
│   │   ├── scheduler/                  # Task scheduler
│   │   └── script/                     # GraalVM script engine
│   │
│   └── modules/                        # Built-in modules
│       └── demo/                       # Demo module
│           └── DemoModule.java
│
├── src/main/resources/
│   ├── plugin.yml
│   ├── config.yml
│   ├── messages.yml
│   └── modules/demo/module.yml
│
├── scripts/examples/                   # JavaScript examples
│   ├── custom_item_example.js
│   ├── scheduler_example.js
│   └── simple_command.js
│
├── README.md                           # This file
├── ARCHITECTURE.md                     # Architecture documentation
├── BUILD.md                            # Build instructions
├── INSTALL.md                          # Installation guide
└── pom.xml                             # Maven configuration
```

## 🚀 Quick Start

### Prerequisites

- **Java 21+** (required for GraalVM)
- **Maven 3.8+**
- **Paper 1.21.8+**

### Build

```bash
mvn clean package
```

### Install

```bash
cp target/FrameworkPlugin-1.0.0.jar /path/to/server/plugins/
```

### Run

Start your Paper server. The plugin will:
1. Initialize DI container
2. Load core services
3. Load modules (demo module included)
4. Load JavaScript scripts

## 📚 Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detailed architecture documentation
- **[BUILD.md](BUILD.md)** - Build and development guide
- **[INSTALL.md](INSTALL.md)** - Installation instructions
- **[PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)** - Project organization

## 🎮 Demo Module

The included demo module showcases framework capabilities:

### Magic Wand Item
- Right-click to teleport forward 5 blocks
- Particle effects and sounds
- Cooldown system
- Given to new players automatically

### /fly Command
- Toggle flight mode
- Sound feedback
- Permission-based

### Welcome Message
- Customizable via config
- Sent on player join

## 💻 JavaScript API

### Example: Custom Item

```javascript
// Register a fire sword
API.registerItem(
    "fire_sword",
    "DIAMOND_SWORD",
    "&c&lFire Sword",
    "&7Sets enemies on fire",
    "&6Epic Item"
);

// Give to player
var player = API.getPlayer("PlayerName");
API.giveItem(player, "fire_sword", 1);
```

### Example: Scheduler

```javascript
// Run after 5 seconds
API.runLater(function() {
    API.broadcast("&aHello World!");
}, 100); // 100 ticks = 5 seconds

// Repeating task
API.runTimer(function() {
    console.log("This runs every 30 seconds");
}, 600, 600);
```

### Example: Player Management

```javascript
// Get online players
var players = API.getOnlinePlayers();

// Send message
players.forEach(function(player) {
    API.sendMessage(player, "&6Welcome!");
});

// Broadcast
API.broadcast("&aServer announcement!");
```

## 🏗️ Creating Modules

### 1. Create module.yml

```yaml
id: mymodule
name: My Module
version: 1.0.0
description: My custom module
authors: [YourName]
dependencies: []
soft-dependencies: []
load-order: STARTUP
enabled: true
```

### 2. Create Module Class

```java
package com.myplugin.modules.mymodule;

import com.myplugin.api.module.ModuleDescriptor;
import com.myplugin.core.module.BaseModule;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class MyModule extends BaseModule {
    
    public MyModule(ModuleDescriptor descriptor, Path dataFolder) {
        super(descriptor, dataFolder);
    }
    
    @Override
    public CompletableFuture<Void> onEnable() {
        return super.onEnable().thenRun(() -> {
            log("My module enabled!");
            // Your initialization code
        });
    }
    
    @Override
    public CompletableFuture<Void> onDisable() {
        return super.onDisable().thenRun(() -> {
            log("My module disabled!");
            // Cleanup code
        });
    }
}
```

### 3. Place in modules/ folder

```
plugins/FrameworkPlugin/modules/mymodule/
├── module.yml
└── config.yml
```

## 🔧 Configuration

### Main Config (config.yml)

```yaml
settings:
  debug: false
  language: en

modules:
  auto-load: true
  
scripting:
  enabled: true
  sandbox: true
  timeout: 5000

performance:
  async-loading: true
  cache-enabled: true
```

## 🎯 Architecture Highlights

### Dependency Injection

```java
Container container = Container.getInstance();
container.registerSingleton(EventBus.class, new EventBusImpl());

EventBus eventBus = container.resolve(EventBus.class).orElseThrow();
```

### Event Bus

```java
eventBus.subscribe(MyEvent.class, event -> {
    // Handle event
}, EventBus.EventPriority.NORMAL);

eventBus.post(new MyEvent()).join();
```

### Module System

- **Dependency Resolution**: Topological sorting
- **Load Order**: STARTUP → POSTWORLD → LAZY
- **Hot Reload**: Reload without restart
- **Isolation**: Each module has isolated state

### Script Engine

- **Sandboxed**: No file I/O, restricted class access
- **Hot Reload**: Reload scripts without restart
- **Error Isolation**: Script errors don't crash plugin
- **Performance**: Scripts compile once, execute many times

## 🔒 Security

- **Sandboxed Scripts**: No access to file system or dangerous APIs
- **Class Whitelisting**: Only allowed classes accessible
- **Permission System**: Fine-grained permissions
- **Input Validation**: All inputs validated
- **Error Isolation**: Errors contained to modules

## ⚡ Performance

- **Async Operations**: All I/O operations async
- **Lazy Loading**: Load on demand
- **Caching**: Frequently used data cached
- **Object Pooling**: Reuse expensive objects
- **Batch Operations**: Batch saves and updates

## 🧪 Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report

# Integration tests
mvn verify
```

## 📊 Monitoring

The framework provides:
- Module load times
- Script execution times
- Event processing metrics
- Memory usage tracking
- Error rates

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Follow code style (Google Java Style)
4. Write tests
5. Submit pull request

## 📝 License

This project is provided as-is for educational and production use.

## 🆘 Support

### Common Issues

**Module won't load**
- Check module.yml syntax
- Verify dependencies
- Check logs for errors

**Script errors**
- Enable debug mode
- Check script syntax
- Verify API usage

**Performance issues**
- Enable profiling
- Check async operations
- Review module count

### Getting Help

1. Check documentation
2. Review examples
3. Enable debug logging
4. Check server logs

## 🎓 Learning Resources

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Paper API Docs](https://docs.papermc.io/)
- [GraalVM Docs](https://www.graalvm.org/latest/docs/)

## 🚀 Roadmap

- [ ] Web dashboard
- [ ] Database integration (MySQL, PostgreSQL)
- [ ] Metrics system (Prometheus)
- [ ] Plugin marketplace
- [ ] REST API
- [ ] Distributed modules
- [ ] Hot code reload

## ✨ Credits

Built with:
- Paper API
- GraalVM
- Java 21
- Maven

---

**Version**: 1.0.0  
**Compatibility**: Paper 1.21.8+, Java 21+  
**Architecture**: Clean Architecture + SOLID  
**Status**: Production Ready ✅
