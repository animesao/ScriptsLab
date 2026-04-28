# 📝 Команды ScriptsLab

Справочник по всем командам плагина ScriptsLab для управления сервером.

---

## Основные команды

### /scriptslab

Главная команда плагина для управления и получения информации.

**Aliases**: `/sl`, `/slab`

| Подкоманда | Описание | Права | Пример |
|------------|-----------|--------|---------|
| `reload` | Перезагрузить плагин | `scriptslab.reload` | `/scriptslab reload` |
| `info` | Информация о плагине | `scriptslab.use` | `/scriptslab info` |
| `help` | Показать помощь | `scriptslab.use` | `/scriptslab help` |

#### Примеры использования

```
/scriptslab reload          # Перезагрузить плагин
/scriptslab info           # Показать информацию о плагине
/scriptslab help           # Показать список команд
```

---

### /module

Управление модулями плагина.

| Подкоманда | Описание | Права | Пример |
|------------|-----------|--------|---------|
| `list` | Список всех модулей | `scriptslab.use` | `/module list` |
| `enable <name>` | Включить модуль | `scriptslab.module` | `/module enable demo` |
| `disable <name>` | Выключить модуль | `scriptslab.module` | `/module disable demo` |
| `reload <name>` | Перезагрузить модуль | `scriptslab.module` | `/module reload demo` |

#### Примеры использования

```
/module list                # Показать все модули
/module enable demo        # Включить модуль demo
/module disable demo      # Выключить модуль demo
/module reload demo       # Перезагрузить модуль demo
```

---

### /script

Управление скриптами.

| Подкоманда | Описание | Права | Пример |
|------------|-----------|--------|---------|
| `list` | Список всех скриптов | `scriptslab.use` | `/script list` |
| `reload` | Перезагрузить все скрипты | `scriptslab.script` | `/script reload` |
| `reload <name>` | Перезагрузить конкретный скрипт | `scriptslab.script` | `/script reload heal` |
| `info <name>` | Информация о скрипте | `scriptslab.use` | `/script info heal` |

#### Примеры использования

```
/script list               # Показать все скрипты
/script reload             # Перезагрузить все скрипты
/script reload heal      # Перезагрузить скрипт heal.js
/script info heal        # Показать информацию о скрипте heal.js
```

---

## Команды модулей

Каждый модуль может добавлять свои команды.см. документацию конкретного модуля.

---

## Команды, созданные пользователями

ScriptsLab позволяет создавать свои команды через JavaScript. Подробности в [Script API](script-api.md).

### Примеры пользовательских команд

| Команда | Описание | Скрипт |
|---------|-----------|--------|
| `/heal` | Восстановить здоровье | `heal_command.js` |
| `/fly` | Включить/выключить полёт | `fly_command.js` |
| `/getlightningsword` | Получить легендарный меч | `custom_sword.js` |

---

## Синтаксис команд

### Обозначения

| Обозначение | Значение |
|-------------|----------|
| `<required>` | Обязательный аргумент |
| `[optional]` | Опциональный аргумент |
| `arg1\|arg2` | Выбор из нескольких значений |

### Примеры синтаксиса

```
/module <list|enable|disable|reload> [name]
      │                         │
      │                         └── Опциональный параметр
      │                            (имя модуля)
      │
      └── Обязательный параметр (выбор действия)

[scriptslab help]
      │
      └── Опциональный параметр (тема помощи)
```

---

## Вывод сообщений

### /scriptslab info

При выполнении `/scriptslab info` выводится:

```
╔══════════════════════════════════════════╗
║         ScriptsLab Information      ║
╚══════════════════════════════════════════╝
Version: 1.0.0
Authors: ScriptsLab Team
Modules loaded: X
Modules enabled: X
Scripts loaded: X
Custom items: X
Active tasks: X
```

### /module list

При выполнении `/module list` выводится:

```
╔══════════════════════════════════════╗
║           Loaded Modules            ║
╚══════════════════════════════════════╝
demo v1.0.0 ✓ Enabled
economy v1.0.0 ✓ Enabled
...

Total: 2 modules
```

### /script list

При выполнении `/script list` выводится:

```
╔═══════════════════════════════════════╗
║           Loaded Scripts           ║
╚══════════════════════════════════════╝
heal_command.js ✓ OK (executed 10 times)
fly_command.js ✓ OK (executed 5 times)
custom_sword.js ✓ OK (executed 0 times)

Total: 3 scripts
```

---

## Сообщения об ошибках

### Команда не найдена

```
[cUnknown command. Use /scriptslab help]
```

### Нет прав

```
[cYou don't have permission to do that!]
```

### Модуль не найден

```
[cModule not found: module_name]
```

### Скрипт не найден

```
[cScript not found: script_name]
```

---

## автоматическое дополнение (tab-complete)

ScriptsLab поддерживает автодополнение для:

- `/module enable <tab>` - имена модулей
- `/module disable <tab>` - имена модулей
- `/module reload <tab>` - имена модулей
- `/script reload <tab>` - имена скриптов
- `/script info <tab>` - имена скриптов

---

## Рекомендации по использованию

### Для игроков

```
/scriptslab help     # Получить помощь
/scriptslab info    # Информация о плагине
/module list        # Список модулей
/script list       # Список скриптов
```

### Для модераторов

```
/module list                       # Список модулей
/module enable <module>          # Включить модуль
/module disable <module>         # Выключить модуль
/script list                     # Список скриптов
```

### Для администра��оров

```
/scriptslab reload               # Перезагрузить плагин
/module reload <module>          # Перезагрузить модуль
/script reload                   # Перезагрузить все скрипты
/script reload <script>          # Перезагрузить конкретный скрипт
```

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Права](permissions.md) | Настройка прав доступа |
| [Script API](script-api.md) | API для создания команд |
| [Примеры](examples/commands.md) | Примеры команд |

---

# 📝 ScriptsLab Commands (English)

Reference for all ScriptsLab plugin commands for server management.

---

## Main Commands

### /scriptslab

Main plugin command for management and information.

**Aliases**: `/sl`, `/slab`

| Subcommand | Description | Permission | Example |
|------------|-----------|--------|---------|
| `reload` | Reload plugin | `scriptslab.reload` | `/scriptslab reload` |
| `info` | Plugin information | `scriptslab.use` | `/scriptslab info` |
| `help` | Show help | `scriptslab.use` | `/scriptslab help` |

#### Usage Examples

```
/scriptslab reload          # Reload plugin
/scriptslab info           # Show plugin info
/scriptslab help           # Show command list
```

---

### /module

Manage plugin modules.

| Subcommand | Description | Permission | Example |
|------------|-----------|--------|---------|
| `list` | List all modules | `scriptslab.use` | `/module list` |
| `enable <name>` | Enable module | `scriptslab.module` | `/module enable demo` |
| `disable <name>` | Disable module | `scriptslab.module` | `/module disable demo` |
| `reload <name>` | Reload module | `scriptslab.module` | `/module reload demo` |

#### Usage Examples

```
/module list                # Show all modules
/module enable demo        # Enable demo module
/module disable demo      # Disable demo module
/module reload demo       # Reload demo module
```

---

### /script

Manage scripts.

| Subcommand | Description | Permission | Example |
|------------|-----------|--------|---------|
| `list` | List all scripts | `scriptslab.use` | `/script list` |
| `reload` | Reload all scripts | `scriptslab.script` | `/script reload` |
| `reload <name>` | Reload specific script | `scriptslab.script` | `/script reload heal` |
| `info <name>` | Script information | `scriptslab.use` | `/script info heal` |

#### Usage Examples

```
/script list               # Show all scripts
/script reload             # Reload all scripts
/script reload heal      # Reload heal.js script
/script info heal        # Show heal.js info
```

---

## Module Commands

Each module can add its own commands. See specific module documentation.

---

## User-Created Commands

ScriptsLab allows creating custom commands via JavaScript. See [Script API](script-api.md).

### Custom Command Examples

| Command | Description | Script |
|---------|-----------|--------|
| `/heal` | Restore health | `heal_command.js` |
| `/fly` | Toggle flight | `fly_command.js` |
| `/getlightningsword` | Get legendary sword | `custom_sword.js` |

---

## Command Syntax

### Notations

| Notation | Meaning |
|-------------|----------|
| `<required>` | Required argument |
| `[optional]` | Optional argument |
| `arg1\|arg2` | Choice between values |

### Syntax Examples

```
/module <list|enable|disable|reload> [name]
      │                         │
      │                         └── Optional parameter
      │                            (module name)
      │
      └── Required parameter (action choice)

[scriptslab help]
      │
      └── Optional parameter (help topic)
```

---

## Output Messages

### /scriptslab info

When executing `/scriptslab info`:

```
╔══════════════════════════════════════════╗
║         ScriptsLab Information      ║
╚══════════════════════════════════════════╝
Version: 1.0.0
Authors: ScriptsLab Team
Modules loaded: X
Modules enabled: X
Scripts loaded: X
Custom items: X
Active tasks: X
```

### /module list

When executing `/module list`:

```
╔════════════════════════════════════╗
║           Loaded Modules            ║
╚════════════════════════════════════╝
demo v1.0.0 ✓ Enabled
economy v1.0.0 ✓ Enabled
...

Total: 2 modules
```

### /script list

When executing `/script list`:

```
╔═════════════════════════════════════╗
║           Loaded Scripts           ║
╚════════════════════════════════════╝
heal_command.js ✓ OK (executed 10 times)
fly_command.js ✓ OK (executed 5 times)
custom_sword.js ✓ OK (executed 0 times)

Total: 3 scripts
```

---

## Error Messages

### Command Not Found

```
[Unknown command. Use /scriptslab help]
```

### No Permission

```
[You don't have permission to do that!]
```

### Module Not Found

```
[Module not found: module_name]
```

### Script Not Found

```
[Script not found: script_name]
```

---

## Tab Completion

ScriptsLab supports tab completion for:

- `/module enable <tab>` - module names
- `/module disable <tab>` - module names
- `/module reload <tab>` - module names
- `/script reload <tab>` - script names
- `/script info <tab>` - script names

---

## Usage Recommendations

### For Players

```
/scriptslab help     # Get help
/scriptslab info    # Plugin info
/module list        # List modules
/script list       # List scripts
```

### For Moderators

```
/module list                       # List modules
/module enable <module>          # Enable module
/module disable <module>         # Disable module
/script list                     # List scripts
```

### For Administrators

```
/scriptslab reload               # Reload plugin
/module reload <module>          # Reload module
/script reload                   # Reload all scripts
/script reload <script>          # Reload specific script
```

---

## Next Steps

| Step | Description |
|-----|----------|
| [Permissions](permissions.md) | Access rights setup |
| [Script API](script-api.md) | API for creating commands |
| [Examples](examples/commands.md) | Command examples |