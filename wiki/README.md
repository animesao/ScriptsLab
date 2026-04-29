# ⚡ ScriptsLab Wiki

Добро пожаловать в полную документацию плагина ScriptsLab!

## Для кого эта документация?

Эта Wiki написана для всех - как для новичков, которые впервые сталкиваются с плагинами для Minecraft серверов, так и для опытных разработчиков. Здесь вы найдёте:

- 📥 **Установка** - Пошаговая инструкция по установке
- ⚙️ **Настройка** - Конфигурация плагина
- 📝 **API** - Полное руководство по JavaScript API
- 💡 **Примеры** - Готовые скрипты с объяснениями
- 🔧 **Решение проблем** - Ответы на частые вопросы

---

## Быстрые ссылки

| Раздел | Описание |
|--------|----------|
| [Установка](installation.md) | Как установить плагин на сервер |
| [Настройка](configuration.md) | Конфигурация config.yml |
| [Команды](commands.md) | Команды сервера для управления плагином |
| [Права](permissions.md) | Система разрешений |
| [Script API](script-api.md) | Полный справочник JavaScript API |
| [Модули](modules.md) | Система модулей |
| [Примеры](examples/) | Готовые примеры скриптов |
| [FAQ](faq.md) | Часто задаваемые вопросы |
| [Wiki-Site](https://scriptslab.spcfy.eu/wiki) | официальный сайт |

---

## Что такое ScriptsLab?

ScriptsLab - это мощный плагин-фреймворк для серверов Minecraft (Paper/Spigot), который позволяет создавать кастомный игровой контент с помощью **JavaScript**.

### Почему ScriptsLab?

| Возможность | Описание |
|------------|-----------|
| 🚀 **Hot-Reload** | Перезагрузка скриптов без перезапуска сервера |
| 🎯 **Модульность** | Организация кода в переиспользуемые модули |
| ⚡ **Производительность** | Работает на движке GraalVM JavaScript |
| 🔒 **Безопасность** | Песочница (sandbox) для защиты сервера |
| 🎨 **Богатый API** | Команды, события, предметы, хранилище, планировщик |
| 🛠️ **Developer Friendly** | Современный JavaScript с поддержкой IDE |

### Основные функции

- **Система команд** - Регистрация своих команд с правами доступа
- **Обработка событий** - Прослушивание любых событий Bukkit/Paper
- **Кастомные предметы** - Создание предметов с способностями и атрибутами
- **Хранилище** - YAML-based постоянное сохранение данных
- **Планировщик задач** - Синхронное/асинхронное планирование задач
- **Сбор метрик** - Встроенный мониторинг производительности
- **Модульная система** - Горячая загрузка модулей плагинов

---

## Требования

| Требование | Версия |
|-------------|--------|
| Minecraft Server | Paper 1.20.4+ (или совместимый Spigot форк) |
| Java | 17 или выше |
| Минимальная память | 2GB RAM |

---

## Быстрый старт

### 1. Установите плагин

Скачайте `ScriptsLab-1.0.0.jar` и поместите в папку `plugins/` вашего сервера.

### 2. Создайте первый скрипт

Создайте файл `plugins/ScriptsLab/scripts/hello.js`:

```javascript
Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aПривет, ' + sender.getName() + '!');
}, 'scriptslab.hello');

Console.log('Команда /hello зарегистрирована!');
```

### 3. Используйте команду

В игре напишите `/hello` - вы получите приветственное сообщение!

---

## Структура файлов плагина

```
plugins/ScriptsLab/
├── config.yml          # Основная конфигурация
├── messages.yml      # Сообщения плагина
├── scripts/         # Ваши JavaScript скрипты
│   ├── hello.js
│   └── examples/
│       └── ...
├── modules/         # Модули плагина
│   └── demo/
│       └── module.yml
└── storage/        # Сохранённые данные (YAML)
```

---

## Перевод цветовых кодов

В ScriptsLab поддерживаются как старые коды (`&`), так и новые (MiniMessage):

| Код | Цвет | Пример |
|-----|------|-------|
| &0 | Чёрный | §0текст |
| &1 | Тёмно-синий | §1текст |
| &2 | Тёмно-зелёный | §2текст |
| &3 | Тёмно-голубой | §3текст |
| &4 | Тёмно-красный | §4текст |
| &5 | Фиолетовый | §5текст |
| &6 | Золотой | §6текст |
| &7 | Серый | §7текст |
| &8 | Тёмно-серый | §8текст |
| &9 | Синий | §9текст |
| &a | Зелёный | §aтекст |
| &b | Голубой | §bтекст |
| &c | Красный | §cтекст |
| &d | Светло-фиолетовый | §dтекст |
| &e | Жёлтый | §eтекст |
| &f | Белый | §fтекст |
| &l | Жирный | §lтекст |
| &o | Курсив | §oтекст |
| &n | Подчёркнутый | §nтекст |
| &m | Зачёркнутый | §mтекст |
| &k | Мигающий | §kтекст |

---

## Поддержка и помощь

- **GitHub Issues**: Сообщить об ошибке
- **GitHub Discussions**: Задать вопрос
- **Discord**: Присоединиться к сообществу

---

## Лицензия

ScriptsLab распространяется под лицензией **MIT** - подробности в файле [LICENSE](../../LICENSE).

---

**Сделано с ❤️ для Minecraft сообщества**

⭐ Поставьте звёздочку на GitHub, если плагин вам полезен!

---

# ⚡ ScriptsLab Wiki (English)

Welcome to the complete documentation for ScriptsLab plugin!

## Who is this documentation for?

This Wiki is written for everyone - from beginners who are new to Minecraft server plugins, to experienced developers. Here you will find:

- 📥 **Installation** - Step-by-step installation guide
- ⚙️ **Configuration** - Plugin configuration
- 📝 **API** - Complete JavaScript API reference
- 💡 **Examples** - Ready-to-use scripts with explanations
- 🔧 **Troubleshooting** - FAQ and common issues

---

## Quick Links

| Section | Description |
|--------|----------|
| [Installation](installation.md) | How to install the plugin |
| [Configuration](configuration.md) | config.yml setup |
| [Commands](commands.md) | Server commands |
| [Permissions](permissions.md) | Permission system |
| [Script API](script-api.md) | JavaScript API reference |
| [Modules](modules.md) | Module system |
| [Examples](examples/) | Script examples |
| [FAQ](faq.md) | Frequently asked questions |
| [Wiki-Site](https://scriptslab.spcfy.eu/wiki) | official website |

---

## What is ScriptsLab?

ScriptsLab is a powerful plugin framework for Minecraft servers (Paper/Spigot) that allows you to create custom gameplay content using **JavaScript**.

### Why ScriptsLab?

| Feature | Description |
|------------|-----------|
| 🚀 **Hot-Reload** | Reload scripts without server restart |
| 🎯 **Modularity** | Organize code into reusable modules |
| ⚡ **Performance** | Powered by GraalVM JavaScript engine |
| 🔒 **Security** | Sandbox for server protection |
| 🎨 **Rich API** | Commands, events, items, storage, scheduler |
| 🛠️ **Developer Friendly** | Modern JavaScript with IDE support |

### Core Features

- **Command System** - Register custom commands with permissions
- **Event Handling** - Listen to any Bukkit/Paper events
- **Custom Items** - Create items with abilities and attributes
- **Storage** - YAML-based persistent data storage
- **Task Scheduler** - Sync/async task scheduling
- **Metrics Collection** - Built-in performance monitoring
- **Module System** - Hot-loadable plugin modules

---

## Requirements

| Requirement | Version |
|-------------|--------|
| Minecraft Server | Paper 1.20.4+ (or compatible Spigot fork) |
| Java | 17 or higher |
| Minimum Memory | 2GB RAM |

---

## Quick Start

### 1. Install the Plugin

Download `ScriptsLab-1.0.0.jar` and place it in your server's `plugins/` folder.

### 2. Create Your First Script

Create `plugins/ScriptsLab/scripts/hello.js`:

```javascript
Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aHello, ' + sender.getName() + '!');
}, 'scriptslab.hello');

Console.log('Hello command registered!');
```

### 3. Use the Command

In-game, type `/hello` - you'll get a welcome message!

---

## Plugin File Structure

```
plugins/ScriptsLab/
├── config.yml          # Main configuration
├── messages.yml      # Plugin messages
├── scripts/         # Your JavaScript scripts
│   ├── hello.js
│   └── examples/
│       └── ...
├── modules/         # Plugin modules
│   └── demo/
│       └── module.yml
└── storage/        # Saved data (YAML)
```

---

## Color Code Translation

ScriptsLab supports both legacy (`&`) and modern (MiniMessage) color codes:

| Code | Color | Example |
|-----|------|-------|
| &0 | Black | §0text |
| &1 | Dark Blue | §1text |
| &2 | Dark Green | §2text |
| &3 | Dark Aqua | §3text |
| &4 | Dark Red | §4text |
| &5 | Dark Purple | §5text |
| &6 | Gold | §6text |
| &7 | Gray | §7text |
| &8 | Dark Gray | §8text |
| &9 | Blue | §9text |
| &a | Green | §atext |
| &b | Aqua | §btext |
| &c | Red | §ctext |
| &d | Light Purple | §dtext |
| &e | Yellow | §etext |
| &f | White | §ftext |
| &l | Bold | §ltext |
| &o | Italic | §otext |
| &n | Underlined | §ntext |
| &m | Strikethrough | §mtext |
| &k | Magic | §ktext |

---

## Support

- **GitHub Issues**: Report a bug
- **GitHub Discussions**: Ask a question
- **Discord**: Join the community

---

## License

ScriptsLab is distributed under the **MIT** license - see [LICENSE](../../LICENSE) for details.

---

**Made with ❤️ for the Minecraft community**

⭐ Star us on GitHub if you find the plugin useful!
