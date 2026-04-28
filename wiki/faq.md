# ❓ Часто задаваемые вопросы (FAQ)

Ответы на самые популярные вопросы о ScriptsLab.

---

## Общие вопросы

### Что такое ScriptsLab?

ScriptsLab - это плагин-фреймворк для Paper/Spigot серверов, который позволяет создавать кастомный контент с помощью JavaScript. Работает на движке GraalVM.

### Какие версии Minecraft поддерживаются?

| Версия | Статус |
|--------|--------|
| 1.20.4 | ✅ Поддерживается |
| 1.21+ | ✅ Поддерживается |
| 1.19 и ниже | ❌ Не поддерживается |

### Какие серверные типы поддерживаются?

- **Paper** (рекомендуется)
- **Spigot**
- **Pufferfish**

> **Важно**: CraftBukkit не поддерживается!

---

## Установка

### Какие системные требования?

| Требование | Минимум | Рекомендуется |
|-------------|---------|---------------|
| Java | 17 | 17 LTS |
| RAM | 2GB | 4GB+ |
| CPU | 1 ядро | 2+ ядра |

### Как установить плагин?

1. Скачайте `ScriptsLab-1.0.0.jar`
2. Поместите в папку `plugins/`
3. Перезапустите сервер
4. Готово!

### Нужно ли пересобирать плагин из исходников?

Нет, готовый JAR уже включает GraalVM. Просто скачайте и используйте.

---

## Скрипты

### Где хранятся скрипты?

```
plugins/ScriptsLab/scripts/
├── hello.js
├── examples/
└── ...
```

### Как создать свой первый скрипт?

1. Создайте файл в папке `scripts/`
2. Напишите код на JavaScript
3. Выполните `/script reload`
4. Используйте команду!

**Пример**:
```javascript
Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aПривет!');
});
```

### Как перезагрузить скрипты?

```
/script reload
```

### Поддерживается ли автоперезагрузка?

Да! Включите в `config.yml`:
```yaml
scripts:
  auto-reload: true
```

### Какие ограничения у скриптов?

| Ограничение | Значение |
|-------------|----------|
| Таймаут | 5 секунд (настраивается) |
| Память | 128MB (настраивается) |
| Песочница | Ограниченный доступ |

---

## API

### Какие объекты API доступны?

| Объект | Описание |
|--------|---------|
| `Console` | Логирование |
| `Commands` | Команды |
| `Events` | События |
| `Scheduler` | Задачи |
| `Players` | Игроки |
| `Server` | Сервер |
| `World` | Миры |
| `Items` | Предметы |
| `Storage` | Хранилище |

### Можно ли использовать Java классы?

Да! ScriptsLab позволяет использовать Java:
```javascript
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
```

---

## Команды

### Как зарегистрировать команду?

```javascript
Commands.register('имя', function(sender, args) {
    // код
}, 'право');
```

### Как проверить, что отправитель - игрок?

```javascript
if (!sender.isPlayer()) {
    sender.sendMessage('§cТолько для игроков!');
    return;
}
```

---

## События

### Как обработать событие?

```javascript
Events.on('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    player.sendMessage('§aДобро пожаловать!');
});
```

### Какие события доступны?

- `PlayerJoinEvent` - вход
- `PlayerQuitEvent` - выход
- `PlayerChatEvent` - чат
- `PlayerDeathEvent` - смерть
- `EntityDamageByEntityEvent` - атака
- И многие другие...

---

## Предметы

### Как создать кастомный предмет?

```javascript
var item = new ItemStack(Material.DIAMOND_SWORD);
var meta = item.getItemMeta();
meta.setDisplayName('§cМой меч');
item.setItemMeta(meta);
player.getInventory().addItem(item);
```

---

## Хранилище

### Как сохранить данные?

```javascript
Storage.save('ключ', значение);
```

### Как загрузить данные?

```javascript
Storage.load('ключ').then(function(значение) {
    Console.log(значение);
});
```

---

## Модули

### Как создать модуль?

1. Создайте папку `plugins/ScriptsLab/modules/моймодуль/`
2. Создайте `module.yml`
3. Добавьте в `config.yml`

### Как включить/выключить модуль?

```
/module enable <имя>
/module disable <имя>
```

---

## Безопасность

### Что такое песочница (sandbox)?

Песочница ограничивает доступ скриптов к опасным операциям. Включается в `config.yml`:

```yaml
security:
  sandbox-enabled: true
```

### Безопасно ли использовать ScriptsLab?

Да, при правильной настройке:
- Включите `sandbox-enabled` на публичных серверах
- Ограничьте права игроков
- Регулярно обновляйте плагин

---

## Производительность

### Плагин вызывает лаги?

Проверьте:
1. Нет ли бесконечных циклов в скриптах
2. Достаточно ли памяти выделено
3. Слишком ли часто выполняются задачи

### Как оптимизировать?

- Используйте `Scheduler.runTimer` вместо циклов
- Не выполняйте тяжёлые операции в главном потоке
- Ограничьте количество слушателей событий

---

## Ошибки

### "Unknown command"

Выполните `/script list` для проверки загрузки скриптов.

### "You don't have permission"

Проверьте права в `permissions.yml`.

### "Script execution timeout"

Увеличьте таймаут в `config.yml`:
```yaml
scripts:
  timeout: 10000
```

---

## Обновление

### Как обновить плагин?

1. Остановите сервер
2. Замените JAR файл
3. Запустите сервер

### Нужно ли backup?

Да, сделайте backup перед обновлением:
- Папка `plugins/ScriptsLab/`
- База данных (если используется SQLite)

---

## Разработка

### Как стать beta-тестировщиком?

Следите за GitHub Releases.

### Как предложить функцию?

Создайте Issue на GitHub.

---

## Поддержка

### Где получить помощь?

- **GitHub Issues**: сообщить об ошибке
- **GitHub Discussions**: задать вопрос

### Как сообщить об ошибке?

Укажите:
1. Версию ScriptsLab
2. Версию сервера
3. Версию Java
4. Лог ошибки
5. Шаги воспроизведения

---

## Glossary

| Термин | Определение |
|--------|-------------|
| GraalVM | JavaScript движок |
| Sandbox | Песочница безопасности |
| Hot-reload | Горячая перезагрузка |
| API | Интерфейс программирования |

---

## Следующие шаги

| Раздел | Описание |
|--------|----------|
| [Script API](script-api.md) | Полный API |
| [Примеры](examples/) | Готовые примеры |
| [Troubleshooting](troubleshooting.md) | Решение проблем |

---

# ❓ Frequently Asked Questions (English)

Answers to the most popular questions about ScriptsLab.

---

## General Questions

### What is ScriptsLab?

ScriptsLab is a plugin framework for Paper/Spigot servers that allows you to create custom content using JavaScript. Powered by GraalVM engine.

### Which Minecraft versions are supported?

| Version | Status |
|--------|--------|
| 1.20.4 | ✅ Supported |
| 1.21+ | ✅ Supported |
| 1.19 and below | ❌ Not supported |

### Which server types are supported?

- **Paper** (recommended)
- **Spigot**
- **Pufferfish**

> **Important**: CraftBukkit is NOT supported!

---

## Installation

### What are the system requirements?

| Requirement | Minimum | Recommended |
|-------------|---------|---------------|
| Java | 17 | 17 LTS |
| RAM | 2GB | 4GB+ |
| CPU | 1 core | 2+ cores |

### How to install the plugin?

1. Download `ScriptsLab-1.0.0.jar`
2. Place in `plugins/` folder
3. Restart server
4. Done!

### Do I need to rebuild from source?

No, the ready JAR already includes GraalVM. Just download and use.

---

## Scripts

### Where are scripts stored?

```
plugins/ScriptsLab/scripts/
├── hello.js
├── examples/
└── ...
```

### How to create your first script?

1. Create file in `scripts/` folder
2. Write JavaScript code
3. Execute `/script reload`
4. Use the command!

**Example**:
```javascript
Commands.register('hello', function(sender, args) {
    sender.sendMessage('§aHello!');
});
```

### How to reload scripts?

```
/script reload
```

### Is auto-reload supported?

Yes! Enable in `config.yml`:
```yaml
scripts:
  auto-reload: true
```

### What are script limitations?

| Limitation | Value |
|-------------|----------|
| Timeout | 5 seconds (configurable) |
| Memory | 128MB (configurable) |
| Sandbox | Restricted access |

---

## API

### Which API objects are available?

| Object | Description |
|--------|---------|
| `Console` | Logging |
| `Commands` | Commands |
| `Events` | Events |
| `Scheduler` | Tasks |
| `Players` | Players |
| `Server` | Server |
| `World` | Worlds |
| `Items` | Items |
| `Storage` | Storage |

### Can I use Java classes?

Yes! ScriptsLab allows using Java:
```javascript
var Material = Java.type('org.bukkit.Material');
var ItemStack = Java.type('org.bukkit.inventory.ItemStack');
```

---

## Commands

### How to register a command?

```javascript
Commands.register('name', function(sender, args) {
    // code
}, 'permission');
```

### How to check if sender is player?

```javascript
if (!sender.isPlayer()) {
    sender.sendMessage('§cOnly for players!');
    return;
}
```

---

## Events

### How to handle an event?

```javascript
Events.on('PlayerJoinEvent', function(event) {
    var player = event.getPlayer();
    player.sendMessage('§aWelcome!');
});
```

### Which events are available?

- `PlayerJoinEvent` - join
- `PlayerQuitEvent` - quit
- `PlayerChatEvent` - chat
- `PlayerDeathEvent` - death
- `EntityDamageByEntityEvent` - attack
- And many others...

---

## Items

### How to create a custom item?

```javascript
var item = new ItemStack(Material.DIAMOND_SWORD);
var meta = item.getItemMeta();
meta.setDisplayName('§cMy Sword');
item.setItemMeta(meta);
player.getInventory().addItem(item);
```

---

## Storage

### How to save data?

```javascript
Storage.save('key', value);
```

### How to load data?

```javascript
Storage.load('key').then(function(value) {
    Console.log(value);
});
```

---

## Modules

### How to create a module?

1. Create folder `plugins/ScriptsLab/modules/mymodule/`
2. Create `module.yml`
3. Add to `config.yml`

### How to enable/disable a module?

```
/module enable <name>
/module disable <name>
```

---

## Security

### What is sandbox?

Sandbox restricts scripts' access to dangerous operations. Enable in `config.yml`:

```yaml
security:
  sandbox-enabled: true
```

### Is ScriptsLab safe to use?

Yes, with proper configuration:
- Enable `sandbox-enabled` on public servers
- Restrict player permissions
- Regularly update the plugin

---

## Performance

### Plugin causes lag?

Check:
1. No infinite loops in scripts
2. Enough memory allocated
3. Tasks not running too frequently

### How to optimize?

- Use `Scheduler.runTimer` instead of loops
- Don't execute heavy operations on main thread
- Limit event listener count

---

## Errors

### "Unknown command"

Execute `/script list` to check script loading.

### "You don't have permission"

Check permissions in `permissions.yml`.

### "Script execution timeout"

Increase timeout in `config.yml`:
```yaml
scripts:
  timeout: 10000
```

---

## Updates

### How to update the plugin?

1. Stop the server
2. Replace JAR file
3. Start the server

### Do I need a backup?

Yes, make a backup before updating:
- `plugins/ScriptsLab/` folder
- Database (if using SQLite)

---

## Development

### How to become a beta tester?

Follow GitHub Releases.

### How to suggest a feature?

Create an Issue on GitHub.

---

## Support

### Where to get help?

- **GitHub Issues**: Report a bug
- **GitHub Discussions**: Ask a question

### How to report a bug?

Specify:
1. ScriptsLab version
2. Server version
3. Java version
4. Error log
5. Reproduction steps

---

## Glossary

| Term | Definition |
|--------|-------------|
| GraalVM | JavaScript engine |
| Sandbox | Security sandbox |
| Hot-reload | Hot reload |
| API | Programming interface |

---

## Next Steps

| Section | Description |
|--------|----------|
| [Script API](script-api.md) | Full API |
| [Examples](examples/) | Ready-made examples |
| [Troubleshooting](troubleshooting.md) | Problem solving |