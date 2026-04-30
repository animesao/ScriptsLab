# 🔧 Решение проблем

Руководство по устранению распространённых проблем с ScriptsLab.

---

## Общие проблемы

### Плагин не загружается

**Симптомы**: Плагин не отображается в списке `/plugins`

**Причины**:
1. Неправильная версия сервера
2. Конфликт с другим плагином
3. Ошибка в JAR файле

**Решения**:

1. **Проверьте версию сервера**
   ```
   /version
   ```
   Должна быть Paper 1.20.4+ илиSpigot 1.20.4+

2. **Проверьте логи**
   ```
   logs/latest.log
   ```
   Ищите ошибки при загрузке

3. **Удалите其他 плагины**
   Отключите другие плагины и проверьте загрузку

---

### Ошибка "Java version not supported"

**Симптомы**:
```
[ScriptsLab] ERROR: Java 17 required
```

**Решение**:

```bash
# Linux - Проверьте версию Java
java -version

# Установите Java 17
sudo apt install openjdk-17-jdk
```

**Проверка в startup скрипте**:
```bash
#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
java -Xmx4G -Xms2G -jar paper.jar --nogui
```

---

### Ошибка "Plugin does not support this server type"

**Симптомы**:
```
[ScriptsLab] ERROR: Plugin requires Paper
```

**Решение**:
- Используйте Paper, Spigot или Pufferfish
- CraftBukkit не поддерживается!

---

## Проблемы со скриптами

### Скрипт не загружается

**Симптомы**: Команда из скрипта не работает

**Диагностика**:

1. **Проверьте загрузку скриптов**
   ```
   /script list
   ```

2. **Проверьте логи**
   ```
   logs/latest.log
   ```
   Ищите ошибки JavaScript

3. **Включите debug режим**
   ```yaml
   # config.yml
   general:
     debug: true
   ```

**Распространённые причины**:

| Ошибка | Причина | Решение |
|--------|---------|--------|
| `ReferenceError` | Не-defined переменная | Проверьте имя переменной |
| `SyntaxError` | Ошибка синтаксиса | Проверьте скобки и запятые |
| `TypeError` | Неверный тип | Проверьте вызов метода |

---

### Ошибка "timeout" при выполнении

**Симптомы**:
```
[ScriptsLab] ERROR: Script execution timeout
```

**Решение**:

1. **Увеличьте таймаут** в `config.yml`:
   ```yaml
   scripts:
     timeout: 10000  # 10 секунд
   ```

2. **Оптимизируйте скрипт**
   - Избегайте бесконечных циклов
   - Используйте асинхронные операции

---

### Скрипт вызывает лаги сервера

**Симптомы**: Сервер тормозит после загрузки скрипта

**Решение**:

1. **Проверьте циклы**
   ```javascript
   // Плохо
   while (true) {
       // Бесконечный цикл!
   }

   // Хорошо
   Scheduler.runTimer(function() {
       // Код
   }, 0, 20);
   ```

2. **ИспользуйтеrunLater вместопрямых вызовов**
   ```javascript
   // Плохо - блокирует сервер
   heavyFunction();

   // Хорошо - выполняется асинхронно
   Scheduler.runLater(function() {
       heavyFunction();
   }, 0);
   ```

3. **Ограничьте частоту выполнения**
   ```javascript
   // Запускайте не чаще 1 раза в секунду
   Scheduler.runTimer(function() {
       // Код
   }, 0, 20);
   ```

---

## Проблемы с памятью

### OutOfMemoryError

**Симптомы**:
```
java.lang.OutOfMemoryError: Java heap space
```

**Решение**:

1. **Увеличьте память** в startup скрипте:
   ```bash
   java -Xmx6G -Xms4G -jar paper.jar --nogui
   ```

2. **Уменьшите max-memory** в config.yml:
   ```yaml
   scripts:
     max-memory: 64
   ```

3. **Проверьте утечки памяти**
   - Удаляйте слушатели событий при выгрузке скрипта
   - Отменяйте задачи планировщика

---

## Проблемы с правами

### Нет доступа к команде

**Симптомы**: `You don't have permission to do that!`

**Решение**:

1. **Проверьте право**
   ```yaml
   # В config.yml permissions
   permissions:
     scriptslab.use: true
   ```

2. **Добавьте право игроку**
   ```yaml
   # LuckPerms
   lp user NickName permission set scriptslab.heal true
   ```

3. **Добавьте право в группу**
   ```yaml
   # PermissionsEx
   groups:
     player:
       permissions:
         - scriptslab.heal
   ```

---

## Проблемы с командами

### Команда не найдена

**Симптомы**: `Unknown command`

**Решение**:

1. **Проверьте список команд**
   ```
   /script list
   ```

2. **Перезагрузите скрипты**
   ```
   /script reload
   ```

3. **Проверьте название команды**
   ```javascript
   // Регистрация
   Commands.register('hello', function(...) {...});

   // Вызов
   /hello  // Не /heallo
   ```

---

### Команда работает только для игроков

**Симптомы**: `Only players can use this command`

**Решение**:

```javascript
Commands.register('mycommand', function(sender, args) {
    // Проверка на игрока (уже есть)
    if (!sender.isPlayer()) {
        sender.sendMessage('§cOnly for players!');
        return;
    }
    
    // Ваш код
});
```

---

## Проблемы с событиями

### Событие не срабатывает

**Симптомы**: Обработчик события не выполняется

**Диагностика**:

1. **Проверьте имя события**
   ```javascript
   // Правильно
   Events.on('PlayerJoinEvent', function(event) {...});
   
   // Неправильно
   Events.on('PlayerJoin', function(event) {...});
   ```

2. **Проверьте отмену события**
   ```javascript
   event.setCancelled(true);
   ```

3. **Включите debug**
   ```javascript
   Events.on('PlayerJoinEvent', function(event) {
       Console.debug('Событие сработало!');
   });
   ```

---

## Проблемы с предметами

### Кастомный предмет не создаётся

**Симптомы**: Команда получения предмета не работает

**Решение**:

1. **ПроверьтеMaterial**
   ```javascript
   // Правильно
   Material.DIAMOND_SWORD
   
   // Неправильно  
   Material.Diamond_Sword
   ```

2. **Проверьте ItemMeta**
   ```javascript
   var meta = item.getItemMeta();
   if (meta) {
       // Код
   }
   ```

3. **Проверьте логи**
   ```javascript
   Console.log('Создание предмета...');
   // Добавьте логирование
   ```

---

## Проблемы с хранением

### Данные не сохраняются

**Симптомы**: После перезагрузки данные теряются

**Решение**:

1. **Проверьте вызов save**
   ```javascript
   Storage.save('key', value);  // Сохранить
   
   // И загрузку
   Storage.load('key').then(function(value) {
       Console.log(value);
   });
   ```

2. **Проверьтеconfig.yml**
   ```yaml
   storage:
     provider: yaml
     auto-save: true
   ```

---

## Проблемы с GraalVM

### GraalVM не загружается

**Симптомы**:
```
[ScriptsLab] ERROR: Failed to initialize GraalVM
```

**Решение**:

1. **Проверьте JAR файл**
   - Убедитесь, что используете правильный JAR
   - Попробуйте пересобрать

2. **ПроверьтеJava**
   - Нужен Java 17+
   - Убедитесь, что JAVA_HOME указан правильно

---

## Логирование проблем

### Как получить полезную информацию

1. **Включите debug режим**:
   ```yaml
   general:
     debug: true
   ```

2. **Проверьте логи**:
   ```
   logs/latest.log
   ```

3. **Используйте Console.log**:
   ```javascript
   Console.log('Отладка: ' + variable);
   Console.debug('Debug: ' + variable);
   ```

---

## Команды диагностики

| Команда | Описание |
|---------|----------|
| `/scriptslab info` | Информация о плагине |
| `/script list` | Список скриптов |
| `/script reload` | Перезагрузить скрипты |
| `/module list` | Список модулей |

---

## Получение помощи

### Где искать помощь

1. **Логи сервера** - `logs/latest.log`
2. **GitHub Issues** - сообщить об ошибке
3. **Discord** - сообщество

### При сообщении об ошибке

Укажите:
- Версию ScriptsLab
- Версию сервера (Paper/Spigot)
- Версию Java (`java -version`)
- Лог ошибки
- Шаги воспроизведения

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [FAQ](faq.md) | Часто задаваемые вопросы |
| [Script API](script-api.md) | Полный API |
| [Examples](examples/) | Примеры |