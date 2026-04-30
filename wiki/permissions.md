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