# Framework Architecture

## Overview

FrameworkPlugin is a production-grade, modular plugin framework for Minecraft Paper servers. It follows Clean Architecture principles with strict separation of concerns.

## Architecture Layers

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  (Commands, Events, GUI)                │
├─────────────────────────────────────────┤
│           Application Layer             │
│  (Use Cases, Business Logic)            │
├─────────────────────────────────────────┤
│           Domain Layer (API)            │
│  (Interfaces, Entities, Value Objects)  │
├─────────────────────────────────────────┤
│        Infrastructure Layer (Core)      │
│  (Implementations, External Services)   │
└─────────────────────────────────────────┘
```

## Core Components

### 1. Dependency Injection Container

**Location**: `core/di/Container.java`

**Purpose**: Lightweight DI container for service management

**Features**:
- Singleton registration
- Factory registration
- Thread-safe (double-checked locking)
- Type-safe resolution

**Usage**:
```java
Container container = Container.getInstance();
container.registerSingleton(EventBus.class, new EventBusImpl());
EventBus eventBus = container.resolve(EventBus.class).orElseThrow();
```

### 2. Module System

**Location**: `api/module/`, `core/module/`

**Purpose**: Dynamic module loading with dependency resolution

**Features**:
- Topological sorting for load order
- Dependency validation
- Hot reload support
- Lifecycle management (load → enable → disable → unload)

**Module Lifecycle**:
```
┌──────┐    ┌────────┐    ┌─────────┐    ┌──────────┐
│ Load │ -> │ Enable │ -> │ Running │ -> │ Disable  │
└──────┘    └────────┘    └─────────┘    └──────────┘
                              ↓                ↓
                          ┌────────┐      ┌────────┐
                          │ Reload │      │ Unload │
                          └────────┘      └────────┘
```

### 3. Event Bus

**Location**: `api/event/`, `core/event/`

**Purpose**: Custom event system for cross-module communication

**Features**:
- Priority-based event handling
- Cancellable events
- Async event posting
- Type-safe subscriptions

**Event Flow**:
```
Publisher -> EventBus -> [Priority Queue] -> Subscribers
                              ↓
                         [LOWEST → LOW → NORMAL → HIGH → HIGHEST → MONITOR]
```

### 4. Script Engine

**Location**: `api/script/`, `core/script/`

**Purpose**: Sandboxed JavaScript execution using GraalVM

**Features**:
- ECMAScript 2022 support
- Sandboxing (no file I/O, restricted class access)
- Hot reload
- Error isolation
- Execution tracking

**Security Model**:
```
JavaScript Code
      ↓
  Sandbox Layer (GraalVM Context)
      ↓
  API Layer (ScriptAPIImpl)
      ↓
  Plugin Services
```

### 5. Item System

**Location**: `api/item/`, `core/item/`

**Purpose**: Custom items with abilities and NBT data

**Features**:
- Immutable item definitions
- Ability system with cooldowns
- NBT persistence
- Rarity system
- Builder pattern for creation

**Item Structure**:
```
CustomItem
├── ID (unique identifier)
├── Material (base Bukkit material)
├── Display Name
├── Lore
├── Custom Model Data
├── NBT Data (key-value pairs)
├── Abilities (list of ItemAbility)
├── Rarity (COMMON → MYTHIC)
└── Unbreakable flag
```

### 6. Task Scheduler

**Location**: `api/scheduler/`, `core/scheduler/`

**Purpose**: Wrapper around BukkitScheduler with better API

**Features**:
- CompletableFuture support
- TimeUnit-based delays
- Task tracking
- Owner-based cancellation

## Design Patterns

### 1. Dependency Injection
- **Where**: Throughout the framework
- **Why**: Loose coupling, testability, flexibility

### 2. Repository Pattern
- **Where**: Storage layer
- **Why**: Abstract data access, swappable backends

### 3. Observer Pattern
- **Where**: Event bus
- **Why**: Decoupled communication

### 4. Strategy Pattern
- **Where**: Storage providers
- **Why**: Pluggable storage backends

### 5. Builder Pattern
- **Where**: Item creation, GUI building
- **Why**: Fluent API, immutability

### 6. Factory Pattern
- **Where**: Module creation
- **Why**: Encapsulate object creation

### 7. Singleton Pattern
- **Where**: DI container, managers
- **Why**: Single instance, global access (thread-safe)

## Thread Safety

### Concurrency Strategy

1. **Immutable Objects**: All API entities are immutable (records)
2. **Concurrent Collections**: ConcurrentHashMap for all registries
3. **Atomic Operations**: AtomicBoolean, AtomicInteger for state
4. **CompletableFuture**: All async operations return futures
5. **No Shared Mutable State**: Each module has isolated state

### Thread Model

```
Main Thread (Bukkit)
├── Event Handling
├── Command Execution
└── Synchronous Tasks

Async Thread Pool
├── Module Loading
├── Script Execution
├── Storage Operations
└── Heavy Computations
```

## Performance Optimizations

### 1. Lazy Loading
- Modules load on-demand
- Configs load when first accessed
- Scripts compile once, execute many times

### 2. Caching
- Module descriptors cached
- Item definitions cached
- Script contexts reused

### 3. Batch Operations
- Storage batch saves
- Event batch processing
- Cooldown cleanup batching

### 4. Async I/O
- All file operations async
- Database operations async
- Network operations async

### 5. Object Pooling
- Reuse expensive objects
- Minimize allocations
- Reduce GC pressure

## Error Handling

### Strategy

1. **Fail Fast**: Validate early, throw exceptions
2. **Graceful Degradation**: Continue on non-critical errors
3. **Error Isolation**: Module errors don't crash plugin
4. **Detailed Logging**: All errors logged with context
5. **Recovery**: Auto-recovery where possible

### Error Flow

```
Error Occurs
    ↓
Log Error (with context)
    ↓
Notify Affected Components
    ↓
Attempt Recovery
    ↓
If Recovery Fails → Disable Component
    ↓
Continue Plugin Operation
```

## Extension Points

### 1. Custom Modules
Implement `Module` interface or extend `BaseModule`

### 2. Custom Storage Providers
Implement `StorageProvider` interface

### 3. Custom Item Abilities
Implement `ItemAbility` interface

### 4. Custom Events
Implement `PluginEvent` interface

### 5. Script API Extensions
Add methods to `ScriptAPIImpl`

## Testing Strategy

### Unit Tests
- Test individual components in isolation
- Mock dependencies
- Fast execution

### Integration Tests
- Test component interactions
- Use test containers
- Realistic scenarios

### Performance Tests
- Load testing
- Stress testing
- Memory profiling

## Deployment

### Development
```
mvn clean package
→ Copy to test server
→ Reload plugin
→ Test changes
```

### Production
```
mvn clean package -P production
→ Run tests
→ Security scan
→ Deploy to server
→ Monitor logs
```

## Monitoring

### Metrics to Track
- Module load times
- Script execution times
- Event processing times
- Memory usage
- Active tasks count
- Error rates

### Logging Levels
- **SEVERE**: Critical errors
- **WARNING**: Non-critical issues
- **INFO**: Important events
- **FINE**: Debug information
- **FINER**: Detailed debug
- **FINEST**: Trace level

## Future Enhancements

1. **Web Dashboard**: Real-time monitoring and control
2. **Database Integration**: MySQL, PostgreSQL support
3. **Metrics System**: Prometheus integration
4. **Hot Code Reload**: Reload Java code without restart
5. **Distributed Modules**: Load modules from remote sources
6. **API Gateway**: REST API for external integrations
7. **Plugin Marketplace**: Download modules from repository

## Best Practices

### For Module Developers

1. **Use DI Container**: Don't create services manually
2. **Async Operations**: Use CompletableFuture for I/O
3. **Error Handling**: Catch and log all exceptions
4. **Resource Cleanup**: Unregister everything in onDisable
5. **Thread Safety**: Use concurrent collections
6. **Documentation**: Document all public APIs

### For Script Developers

1. **Keep Scripts Small**: One responsibility per script
2. **Error Handling**: Use try-catch in scripts
3. **Avoid Blocking**: Don't block the main thread
4. **Use API**: Don't access Bukkit directly
5. **Test Thoroughly**: Test scripts before deployment

## Troubleshooting

### Common Issues

1. **Module Won't Load**
   - Check dependencies in module.yml
   - Verify module.yml syntax
   - Check logs for errors

2. **Script Errors**
   - Check script syntax
   - Verify API usage
   - Enable debug logging

3. **Performance Issues**
   - Profile with JProfiler
   - Check for memory leaks
   - Review async operations

4. **Thread Deadlocks**
   - Use thread dumps
   - Review synchronization
   - Check for circular waits

## Resources

- [Paper API Documentation](https://docs.papermc.io/)
- [GraalVM Documentation](https://www.graalvm.org/latest/docs/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
