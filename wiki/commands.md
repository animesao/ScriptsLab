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