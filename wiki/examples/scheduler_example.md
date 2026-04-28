# ⏰ Примеры планирования задач

Готовые примеры использования планировщика задач в ScriptsLab.

---

## Таймер с задержкой

Выполняет код один раз после указанной задержки.

```javascript
/**
 * Scheduler.runLater - Выполнить один раз
 * 
 * 20 тиков = 1 секунда
 */

Console.log('Запускаем отложенную задачу...');

// Выполнить через 5 секунд (100 тиков)
Scheduler.runLater(function() {
    Server.broadcast('§eСообщение через 5 секунд!');
    Console.log('Отложенная задача выполнена');
}, 100);

Console.log('Задача запланирована');
```

---

## Повторяющаяся задача

Выполняет код через равные промежутки времени.

```javascript
/**
 * Scheduler.runTimer - Повторяющаяся задача
 */

var counter = 0;

// Выполнять каждую секунду (20 тиков)
var taskId = Scheduler.runTimer(function() {
    counter++;
    Server.broadcast('§eСчётчик: ' + counter);
    
    if (counter >= 10) {
        // Остановить после 10 выполнений
        // Примечание: в текущей версии нужно реализовать отмену
        Console.log('Задача завершена');
    }
}, 0, 20); // 0 = начать сразу, 20 = каждую секунду

Console.log('Повторяющаяся задача запущена, ID: ' + taskId);
```

---

## Автоматические объявления

Показывает разные сообщения через определённые интервалы.

```javascript
/**
 * Автоматические объявления
 */

var messages = [
    '§6[Объявление] §eНе забудьте проголосовать за сервер!',
    '§6[Объявление] §eПосетите наш Discord!',
    '§6[Объявление] §eИспользуйте §a/fly §eдля полёта!',
    '§6[Объявление] §eИспользуйте §a/heal §eдля лечения!'
];

var currentIndex = 0;

// Отправлять каждые 5 минут (6000 тиков)
Scheduler.runTimer(function() {
    Server.broadcast(messages[currentIndex]);
    currentIndex = (currentIndex + 1) % messages.length;
}, 100, 6000); // 100 = первое через 5 секунд, 6000 = каждые 5 минут

Console.log('Автоматические объявления запущены');
```

---

## Автосохранение

Автоматически сохраняет данные игроков.

```javascript
/**
 * Автосохранение данных
 */

Console.log('Запускаем автосохранение...');

// Выполнять каждые 5 минут
Scheduler.runTimer(function() {
    var online = Server.getOnlinePlayers();
    
    if (online.size() > 0) {
        // Сохранение данных каждого игрока
        for (var i = 0; i < online.size(); i++) {
            var player = online.get(i);
            var key = 'player.' + player.getUniqueId() + '.lastsave';
            
            Storage.save(key, Date.now());
        }
        
        Console.log('Сохранено данных для ' + online.size() + ' игроков');
    }
}, 0, 6000); // Каждые 5 минут

Console.log('Автосохранение настроено');
```

---

## Обратный отсчёт

Запускает обратный отсчёт.

```javascript
/**
 * Обратный отсчёт до события
 */

var seconds = 10;

var countdown = Scheduler.runTimer(function() {
    if (seconds > 0) {
        Server.broadcast('§eОсталось: ' + seconds + ' секунд');
        seconds--;
    } else {
        Server.broadcast('§aВремя вышло!');
        // Здесь код после окончания
        Console.log('Обратный отсчёт завершён');
    }
}, 0, 20); // Каждую секунду

Console.log('Обратный отсчёт запущен');
```

---

## Проверка онлайна

Регулярно проверяет количество игроков.

```javascript
/**
 * Проверка онлайна
 */

var checkInterval = function() {
    var players = Server.getOnlinePlayers();
    Console.log('Онлайн: ' + players.size() + ' игроков');
    
    // Проверка минимального онлайна
    if (players.size() === 0) {
        Console.log('На сервере нет игроков!');
    }
};

// Проверять каждые 2 минуты
Scheduler.runTimer(function() {
    checkInterval();
}, 0, 2400);

Console.log('Проверка онлайна настроена');
```

---

## Напоминание о правилах

Напоминает правила через определённые промежутки.

```javascript
/**
 * Напоминание о правилах
 */

var reminders = [
    '§eПомните о правилах сервера: §aNo Griefing!',
    '§eНе спамьте в чате!',
    '§eУважайте других игроков!',
    '§eСмотрите §a/help §eдля списка команд'
];

var index = 0;

// Каждые 10 минут
Scheduler.runTimer(function() {
    Server.broadcast(reminders[index]);
    index = (index + 1) % reminders.length;
}, 0, 12000);

Console.log('Напоминания настроены');
```

---

## Арена PvP - таймер раунда

Управляет раундами на арене.

```javascript
/**
 * Таймер раунда на арене PvP
 */

var roundTime = 300; // 5 минут в секундах (тиках)
var isRoundActive = false;

var roundTimer = Scheduler.runTimer(function() {
    if (!isRoundActive) {
        return;
    }
    
    roundTime--;
    
    // Показывать оставшееся время
    if (roundTime === 60) {
        Server.broadcast('§eДо конца раунда: §c1 минута!');
    } else if (roundTime === 30) {
        Server.broadcast('§eДо конца раунда: §c30 секунд!');
    } else if (roundTime <= 10 && roundTime > 0) {
        Server.broadcast('§c' + roundTime + '...');
    } else if (roundTime === 0) {
        Server.broadcast('§cРаунд завершён!');
        isRoundActive = false;
        
        // Завершение раунда
    }
}, 0, 1); // Каждую секунду

// Функция старта раунда
var startRound = function() {
    roundTime = 300;
    isRoundActive = true;
    Server.broadcast('§aРаунд начался! Удачи!');
};

Console.log('Таймер арены настроен');
```

---

## Интервал между тиками

| Интервал (тиков) | Время | Использование |
|------------------|-------|---------------|
| 20 | 1 секунда | Быстрые проверки |
| 100 | 5 секунд | Сообщения |
| 600 | 30 секунд | Средние задачи |
| 1200 | 1 минута | Автосохранение |
| 6000 | 5 минут | Объявления |
| 12000 | 10 минут | Напоминания |

---

## Отмена задачи

> **Примечание**: В текущей версии отмена задач требует дополнительной реализации. Сохраняйте ID задачи для будущей отмены.

```javascript
// Сохраняем ID задачи
var taskId = Scheduler.runTimer(function() {
    // Код
}, 0, 20);

// Для отмены (требует реализации в будущем)
// Scheduler.cancel(taskId);
```

---

## Лучшие практики

1. **Не выполняйте тяжёлые операции в таймерах** - используйте `runLater` для отложенных задач

2. **Проверяйте онлайн перед отправкой** - избегайте ошибок при отсутствии игроков

3. **Используйте осмысленные интервалы** - не нагружайте сервер слишком частыми задачами

4. **Логируйте важные события** - записывайте выполнение задач в консоль

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Команды](commands.md) | Примеры команд |
| [События](events.md) | Обработка событий |
| [Предметы](items.md) | Кастомные предметы |

---

# ⏰ Scheduler Examples (English)

Ready-to-use task scheduling examples for ScriptsLab.

---

## Delayed Task

Executes code once after specified delay.

```javascript
/**
 * Scheduler.runLater - Execute once
 * 
 * 20 ticks = 1 second
 */
Console.log('Starting delayed task...');

// Execute after 5 seconds (100 ticks)
Scheduler.runLater(function() {
    Server.broadcast('§eMessage after 5 seconds!');
    Console.log('Delayed task executed');
}, 100);

Console.log('Task scheduled');
```

---

## Repeating Task

Executes code at regular intervals.

```javascript
/**
 * Scheduler.runTimer - Repeating task
 */
var counter = 0;

// Execute every second (20 ticks)
var taskId = Scheduler.runTimer(function() {
    counter++;
    Server.broadcast('§eCounter: ' + counter);
    
    if (counter >= 10) {
        // Stop after 10 executions
        // Note: needs implementation in current version
        Console.log('Task completed');
    }
}, 0, 20); // 0 = start immediately, 20 = every second

Console.log('Repeating task started, ID: ' + taskId);
```

---

## Auto Broadcasts

Shows different messages at regular intervals.

```javascript
/**
 * Auto broadcasts
 */
var messages = [
    '§6[Broadcast] §eDon\'t forget to vote for the server!',
    '§6[Broadcast] §eJoin our Discord!',
    '§6[Broadcast] §eUse §a/fly §e for flight!',
    '§6[Broadcast] §eUse §a/heal §e to heal!'
];

var currentIndex = 0;

// Broadcast every 5 minutes (6000 ticks)
Scheduler.runTimer(function() {
    Server.broadcast(messages[currentIndex]);
    currentIndex = (currentIndex + 1) % messages.length;
}, 100, 6000); // 100 = first after 5 sec, 6000 = every 5 min

Console.log('Auto broadcasts started');
```

---

## Auto Save

Automatically saves player data.

```javascript
/**
 * Auto save data
 */
Console.log('Starting auto save...');

// Every 5 minutes
Scheduler.runTimer(function() {
    var online = Server.getOnlinePlayers();
    
    if (online.size() >0) {
        // Save each player's data
        for (var i = 0; i < online.size(); i++) {
            var player = online.get(i);
            var key = 'player.' + player.getUniqueId() + '.lastsave';
            
            Storage.save(key, Date.now());
        }
        
        Console.log('Saved data for ' + online.size() + ' players');
    }
}, 0, 6000); // Every 5 minutes

Console.log('Auto save configured');
```

---

## Countdown Timer

Starts a countdown.

```javascript
/**
 * Countdown to event
 */
var seconds = 10;

var countdown = Scheduler.runTimer(function() {
    if (seconds >0) {
        Server.broadcast('§eTime left: ' + seconds + ' seconds');
        seconds--;
    } else {
        Server.broadcast('§aTime is up!');
        // Code after countdown ends here
        Console.log('Countdown completed');
    }
}, 0, 20); // Every second

Console.log('Countdown started');
```

---

## Online Check

Regularly checks player count.

```javascript
/**
 * Check online status
 */
var checkInterval = function() {
    var players = Server.getOnlinePlayers();
    Console.log('Online: ' + players.size() + ' players');
    
    // Check minimum online
    if (players.size() === 0) {
        Console.log('No players on server!');
    }
};

// Check every 2 minutes
Scheduler.runTimer(function() {
    checkInterval();
}, 0, 2400);

Console.log('Online check configured');
```

---

## Rule Reminders

Reminds rules at intervals.

```javascript
/**
 * Rule reminders
 */
var reminders = [
    '§eRemember server rules: §aNo Griefing!',
    '§eDon\'t spam in chat!',
    '§eRespect other players!',
    '§eCheck §a/help §e for commands'
];

var index = 0;

// Every 10 minutes
Scheduler.runTimer(function() {
    Server.broadcast(reminders[index]);
    index = (index + 1) % reminders.length;
}, 0, 12000);

Console.log('Reminders configured');
```

---

## PvP Arena - Round Timer

Manages rounds in arena.

```javascript
/**
 * PvP Arena round timer
 */
var roundTime = 300; // 5 minutes in seconds (ticks)
var isRoundActive = false;

var roundTimer = Scheduler.runTimer(function() {
    if (!isRoundActive) {
        return;
    }
    
    roundTime--;
    
    // Show remaining time
    if (roundTime === 60) {
        Server.broadcast('§eRound ends in: §c1 minute!');
    } else if (roundTime === 30) {
        Server.broadcast('§eRound ends in: §c30 seconds!');
    } else if (roundTime <= 10 && roundTime >0) {
        Server.broadcast('§c' + roundTime + '...');
    } else if (roundTime === 0) {
        Server.broadcast('§cRound over!');
        isRoundActive = false;
        
        // End round logic here
    }
}, 0, 1); // Every second

// Function to start round
var startRound = function() {
    roundTime = 300;
    isRoundActive = true;
    Server.broadcast('§aRound started! Good luck!');
};

Console.log('Arena timer configured');
```

---

## Tick Intervals

| Interval (ticks) | Time | Usage |
|------------------|-------|---------------|
| 20 | 1 second | Quick checks |
| 100 | 5 seconds | Messages |
| 600 | 30 seconds | Medium tasks |
| 1200 | 1 minute | Auto save |
| 6000 | 5 minutes | Broadcasts |
| 12000 | 10 minutes | Reminders |

---

## Task Cancellation

> **Note**: In current version, task cancellation requires additional implementation. Save task ID for future cancellation.

```javascript
// Save task ID
var taskId = Scheduler.runTimer(function() {
    // Code
}, 0, 20);

// To cancel (requires implementation in future)
// Scheduler.cancel(taskId);
```

---

## Best Practices

1. **Don't execute heavy operations in timers** - use `runLater` for delayed tasks

2. **Check online before sending** - avoid errors when no players

3. **Use meaningful intervals** - don't overload server with too frequent tasks

4. **Log important events** - record task execution to console

---

## Next Steps

| Step | Description |
|-----|----------|
| [Commands](commands.md) | Command examples |
| [Events](events.md) | Event handling |
| [Items](items.md) | Custom items |