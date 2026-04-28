# 🔐 Права доступа ScriptsLab

Полное руководство по системе разрешений (permissions) плагина ScriptsLab.

---

## Обзор системы прав

ScriptsLab использует стандартную систему прав Bukkit/Paper. Права определяют, какие команды и функции доступны игрокам.

### Где хранятся права?

- **LuckPerms**: `plugins/LuckPerms/expressions/`
- **PermissionsEx**: `plugins/PermissionsEx/permissions.yml`
- **Vault** (с другими провайдерами): зависит от провайдера

---

## Список прав ScriptsLab

### Основные права

| Право | Описание | По умолчанию |
|-------|----------|-------------|
| `scriptslab.use` | Использовать базовые команды | true (все) |
| `scriptslab.reload` | Перезагружать плагин | op |
| `scriptslab.module` | Управлять модулями | op |
| `scriptslab.script` | Управлять скриптами | op |
| `scriptslab.admin` | Полный админ-доступ | op |

### Группы прав

| Право | Описание |
|-------|----------|
| `scriptslab.*` | Все права ScriptsLab |

---

## Настройка прав

### Через plugin.yml

Права автоматически регистрируются через `plugin.yml`:

```yaml
permissions:
  scriptslab.*:
    description: All ScriptsLab permissions
    children:
      scriptslab.use: true
      scriptslab.reload: true
      scriptslab.module: true
      scriptslab.script: true
      scriptslab.admin: true

  scriptslab.use:
    description: Use basic ScriptsLab commands
    default: true

  scriptslab.reload:
    description: Reload the plugin
    default: op

  scriptslab.module:
    description: Manage modules
    default: op

  scriptslab.script:
    description: Manage scripts
    default: op

  scriptslab.admin:
    description: Full administrative access
    default: op
```

### Значения по умолчанию

| Значение | Описание | Доступ |
|----------|----------|--------|
| `true` | Разрешено всем | Все игроки |
| `op` | Только для OP | Операторы сервера |
| `false` | Запрещено всем | Никто |

---

## Настройка прав для скриптов

### Пример: Права для команд

При создании команды в скрипте можно указать право:

```javascript
Commands.register('heal', function(sender, args) {
    sender.setHealth(sender.getMaxHealth());
    sender.sendMessage('§aВы исцелены!');
}, 'scriptslab.heal');  // Право для команды
```

### Регистрация прав в скрипте

Права для своих команд добавляйте в `permissions.yml`:

```yaml
# PermissionsEx example
groups:
  player:
    permissions:
      - scriptslab.use
      - scriptslab.heal      # Команда /heal
      - scriptslab.fly       # Команда /fly
      
  vip:
    permissions:
      - scriptslab.use
      - scriptslab.heal
      - scriptslab.fly
      - scriptslab.getlightningsword
      
  admin:
    permissions:
      - scriptslab.*
```

---

## Настройка с LuckPerms

### Создание групп

```bash
# Создать группу VIP
lp creategroup vip

# Добавить право
lp group vip permission set scriptslab.fly true

# Добавить наследование
lp group vip parent add player
```

### Настройка игрока

```bash
# Дать право игроку
lp user NickName permission set scriptslab.admin true

# Добавить в группу
lp user NickName parent set vip
```

### Пример конфигурации LuckPerms

```yaml
# luckperms/groups.yml
groups:
  default:
    weight: 0
    permissions:
      - scriptslab.use
      - bukkit.command.help
      - bukkit.command.list
      
  player:
    weight: 10
    inheritance:
      - default
    permissions:
      - scriptslab.heal
      - scriptslab.fly
      
  vip:
    weight: 20
    inheritance:
      - player
    permissions:
      - scriptslab.getlightningsword
      
  moder:
    weight: 50
    inheritance:
      - player
    permissions:
      - scriptslab.module
      - scriptslab.script
      
  admin:
    weight: 100
    inheritance:
      - moder
    permissions:
      - scriptslab.*
      - bukkit.command.*
```

---

## Настройка с PermissionsEx

### Пример permissions.yml

```yaml
groups:
  Default:
    default: true
    permissions:
      - scriptslab.use
      - bukkit.command.help
      
  Player:
    prefix: '&7[Игрок]'
    permissions:
      - scriptslab.heal
      - scriptslab.fly
      
  VIP:
    prefix: '&6[VIP]'
    inheritance:
      - Player
    permissions:
      - scriptslab.getlightningsword
      
  Moderator:
    prefix: '&c[Модер]'
    permissions:
      - scriptslab.module
      - scriptslab.script
      
  Admin:
    prefix: '&4[Админ]'
    permissions:
      - scriptslab.*
      
users:
  Никнейм:
    group: Admin
```

---

## Права для кастомных команд

### Список стандартных прав

| Команда | Право | Описание |
|---------|-------|----------|
| `/heal` | `scriptslab.heal` | Восстановить здоровье |
| `/fly` | `scriptslab.fly` | Включить полёт |
| `/getlightningsword` | `scriptslab.getlightningsword` | Получить меч |
| `/spawn` | `scriptslab.spawn` | Телепорт на спавн |
| `/warp` | `scriptslab.warp` | Телепорт на варп |

### Добавление своих прав

При регистрации команды:

```javascript
Commands.register('mycommand', function(sender, args) {
    // код команды
}, 'myplugin.mycommand');  // право
```

Затем добавьте право в ваш плагин прав:

```yaml
# Пример для LuckPerms
lp group player permission set myplugin.mycommand true
```

---

## Права для событий

### Автоматические права

Некоторые события могут требовать права:

```javascript
// Команда heal проверяет право scriptslab.heal
Commands.register('heal', function(sender, args) {
    // код
}, 'scriptslab.heal');
```

### Ручная проверка прав

```javascript
Commands.register('adminonly', function(sender, args) {
    if (!sender.hasPermission('myplugin.admin')) {
        sender.sendMessage('§cНет прав!');
        return;
    }
    // код для админов
});
```

---

## Troubleshooting

### Игрок не получает право

Проверьте:

1. **Право существует**: Убедитесь, что право зарегистрировано
2. **Перезагрузка**: Выполните `/luckperms reload` или перезапустите сервер
3. **Приоритет**: Проверьте вес группы
4. **Кэш**: Очистите кэш прав

### Право не работает

1. Проверьте, пр��вильно ли оно написано
2. Убедитесь, что группа/игрок имеет это право
3. Перезагрузите систему прав

### Конфликт прав

Если права конфликтуют:

```bash
# Удалите старое право
lp user NickName permission unset old.permission

# Или измените приоритет
lp user NickName permission set new.permission context=server=lobby
```

---

## Примеры конфигураций

### Простой сервер

```yaml
# permissions.yml
groups:
  default:
    permissions:
      - scriptslab.use
      
  vip:
    permissions:
      - scriptslab.use
      - scriptslab.heal
      - scriptslab.fly
      
  admin:
    permissions:
      - scriptslab.*
```

### Сервер с магазином

```yaml
groups:
  default:
    permissions:
      - scriptslab.use
      - scriptslab.heal
      
  premium:
    permissions:
      - scriptslab.use
      - scriptslab.heal
      - scriptslab.fly
      - scriptslab.getlightningsword
      
  donator:
    permissions:
      - scriptslab.use
      - all_shop_commands
      
  admin:
    permissions:
      - scriptslab.*
```

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Script API](script-api.md) | API для создания команд |
| [Примеры](examples/commands.md) | Примеры команд |
| [troubleshooting](troubleshooting.md) | Решение проблем |

---

## Поддержка

- **LuckPerms Wiki**: [ luckperms.net](https://luckperms.net/wiki)
- **PermissionsEx**: [ github.com/PEXDevs](https://github.com/PEXDevs/PermissionsEx)

---

# 🔐 ScriptsLab Permissions (English)

Complete guide to ScriptsLab permission system.

---

## Permission System Overview

ScriptsLab uses the standard Bukkit/Paper permission system. Permissions determine which commands and functions are available to players.

### Where are permissions stored?

- **LuckPerms**: `plugins/LuckPerms/expressions/`
- **PermissionsEx**: `plugins/PermissionsEx/permissions.yml`
- **Vault** (with other providers): depends on provider

---

## ScriptsLab Permission List

### Core Permissions

| Permission | Description | Default |
|-------|----------|-------------|
| `scriptslab.use` | Use basic commands | true (everyone) |
| `scriptslab.reload` | Reload plugin | op |
| `scriptslab.module` | Manage modules | op |
| `scriptslab.script` | Manage scripts | op |
| `scriptslab.admin` | Full admin access | op |

### Permission Groups

| Permission | Description |
|-------|----------|
| `scriptslab.*` | All ScriptsLab permissions |

---

## Permission Setup

### Via plugin.yml

Permissions are automatically registered via `plugin.yml`:

```yaml
permissions:
  scriptslab.*:
    description: All ScriptsLab permissions
    children:
      scriptslab.use: true
      scriptslab.reload: true
      scriptslab.module: true
      scriptslab.script: true
      scriptslab.admin: true

  scriptslab.use:
    description: Use basic ScriptsLab commands
    default: true

  scriptslab.reload:
    description: Reload the plugin
    default: op

  scriptslab.module:
    description: Manage modules
    default: op

  scriptslab.script:
    description: Manage scripts
    default: op

  scriptslab.admin:
    description: Full administrative access
    default: op
```

### Default Values

| Value | Description | Access |
|----------|----------|--------|
| `true` | Allowed to everyone | All players |
| `op` | Only for OP | Server operators |
| `false` | Denied to everyone | No one |

---

## Setting Up Permissions for Scripts

### Example: Permissions for Commands

When creating a command in a script, you can specify a permission:

```javascript
Commands.register('heal', function(sender, args) {
    sender.setHealth(sender.getMaxHealth());
    sender.sendMessage('§aYou have been healed!');
}, 'scriptslab.heal');  // Permission for command
```

### Registering Permissions in Script

Add permissions for your commands to `permissions.yml`:

```yaml
# PermissionsEx example
groups:
  player:
    permissions:
      - scriptslab.use
      - scriptslab.heal      # /heal command
      - scriptslab.fly       # /fly command
      
  vip:
    permissions:
      - scriptslab.use
      - scriptslab.heal
      - scriptslab.fly
      - scriptslab.getlightningsword
      
  admin:
    permissions:
      - scriptslab.*
```

---

## Setting Up with LuckPerms

### Creating Groups

```bash
# Create VIP group
lp creategroup vip

# Add permission
lp group vip permission set scriptslab.fly true

# Add inheritance
lp group vip parent add player
```

### Setting Up Player

```bash
# Give permission to player
lp user NickName permission set scriptslab.admin true

# Add to group
lp user NickName parent set vip
```

### LuckPerms Configuration Example

```yaml
# luckperms/groups.yml
groups:
  default:
    weight: 0
    permissions:
      - scriptslab.use
      - bukkit.command.help
      - bukkit.command.list
      
  player:
    weight: 10
    inheritance:
      - default
    permissions:
      - scriptslab.heal
      - scriptslab.fly
      
  vip:
    weight: 20
    inheritance:
      - player
    permissions:
      - scriptslab.getlightningsword
      
  moder:
    weight: 50
    inheritance:
      - player
    permissions:
      - scriptslab.module
      - scriptslab.script
      
  admin:
    weight: 100
    inheritance:
      - moder
    permissions:
      - scriptslab.*
      - bukkit.command.*
```

---

## Setting Up with PermissionsEx

### Example permissions.yml

```yaml
groups:
  Default:
    default: true
    permissions:
      - scriptslab.use
      - bukkit.command.help
      
  Player:
    prefix: '&7[Player]'
    permissions:
      - scriptslab.heal
      - scriptslab.fly
      
  VIP:
    prefix: '&6[VIP]'
    inheritance:
      - Player
    permissions:
      - scriptslab.getlightningsword
      
  Moderator:
    prefix: '&c[Mod]'
    permissions:
      - scriptslab.module
      - scriptslab.script
      
  Admin:
    prefix: '&4[Admin]'
    permissions:
      - scriptslab.*
       
users:
  NickName:
    group: Admin
```

---

## Permissions for Custom Commands

### Standard Permission List

| Command | Permission | Description |
|---------|-------|----------|
| `/heal` | `scriptslab.heal` | Restore health |
| `/fly` | `scriptslab.fly` | Toggle flight |
| `/getlightningsword` | `scriptslab.getlightningsword` | Get sword |
| `/spawn` | `scriptslab.spawn` | Teleport to spawn |
| `/warp` | `scriptslab.warp` | Teleport to warp |

### Adding Your Own Permissions

When registering a command:

```javascript
Commands.register('mycommand', function(sender, args) {
    // command code
}, 'myplugin.mycommand');  // permission
```

Then add the permission to your permission plugin:

```yaml
# Example for LuckPerms
lp group player permission set myplugin.mycommand true
```

---

## Permissions for Events

### Automatic Permissions

Some events may require permissions:

```javascript
// heal command checks scriptslab.heal permission
Commands.register('heal', function(sender, args) {
    // code
}, 'scriptslab.heal');
```

### Manual Permission Check

```javascript
Commands.register('adminonly', function(sender, args) {
    if (!sender.hasPermission('myplugin.admin')) {
        sender.sendMessage('§cNo permission!');
        return;
    }
    // code for admins
});
```

---

## Troubleshooting

### Player Not Getting Permission

Check:

1. **Permission exists**: Make sure the permission is registered
2. **Reload**: Run `/luckperms reload` or restart server
3. **Priority**: Check group weight
4. **Cache**: Clear permission cache

### Permission Not Working

1. Check if it's spelled correctly
2. Make sure the group/player has the permission
3. Reload the permission system

### Permission Conflict

If permissions conflict:

```bash
# Remove old permission
lp user NickName permission unset old.permission

# Or change priority
lp user NickName permission set new.permission context=server=lobby
```

---

## Configuration Examples

### Simple Server

```yaml
# permissions.yml
groups:
  default:
    permissions:
      - scriptslab.use
      
  vip:
    permissions:
      - scriptslab.use
      - scriptslab.heal
      - scriptslab.fly
      
  admin:
    permissions:
      - scriptslab.*
```

### Server with Shop

```yaml
groups:
  default:
    permissions:
      - scriptslab.use
      - scriptslab.heal
      
  premium:
    permissions:
      - scriptslab.use
      - scriptslab.heal
      - scriptslab.fly
      - scriptslab.getlightningsword
      
  donator:
    permissions:
      - scriptslab.use
      - all_shop_commands
      
  admin:
    permissions:
      - scriptslab.*
```

---

## Next Steps

| Step | Description |
|-----|----------|
| [Script API](script-api.md) | API for creating commands |
| [Examples](examples/commands.md) | Command examples |
| [Troubleshooting](troubleshooting.md) | Problem solving |

---

## Support

- **LuckPerms Wiki**: [luckperms.net](https://luckperms.net/wiki)
- **PermissionsEx**: [github.com/PEXDevs](https://github.com/PEXDevs/PermissionsEx)