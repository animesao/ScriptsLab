# 🔒 Безопасность ScriptsLab

Руководство по обеспечению безопасности при использовании ScriptsLab.

---

## Песочница (Sandbox)

Песочница — это режим работы скриптов, при котором ограничивается доступ к опасным функциям Java и системы.

### Включение песочницы

```yaml
# config.yml
security:
  sandbox-enabled: true
```

| Значение | Уровень безопасности | Рекомендация |
|-----------|----------------------|----------------|
| `false` | Низкий | Приватные серверы, доверенные скрипты |
| `true` | Высокий | Публичные серверы, непроверенные скрипты |

> **Важно**: На публичных серверах настоятельно рекомендуется использовать `sandbox-enabled: true`!

---

## Разрешенные пакеты (Allowed Packages)

В режиме песочницы скрипты могут использовать только указанные Java-пакеты.

### Настройка

```yaml
security:
  allowed-packages:
    - org.bukkit
    - org.bukkit.entity
    - net.kyori
    - java.util
    - java.lang
    - java.util.concurrent
```

| Пакет | Описание |
|--------|----------|
| `org.bukkit` | Основной API Bukkit |
| `org.bukkit.entity` | Сущности (игроки, мобы) |
| `net.kyori` | MiniMessage и компоненты |
| `java.util` | Утилиты Java (List, Map) |
| `java.lang` | Базовые классы |

---

## Заблокированные операции (Blocked Operations)

Список операций, которые запрещены в песочнице:

```yaml
security:
  blocked-operations:
    - file-write
    - file-delete
    - system-exec
    - network-access
    - reflection
```

| Операция | Описание | Риск |
|---------|-----------|------|
| `file-write` | Запись в файлы | Изменение системы |
| `file-delete` | Удаление файлов | Потеря данных |
| `system-exec` | Выполнение системных команд | RCE уязвимость |
| `network-access` | Сетевые запросы | Утечка данных |
| `reflection` | Java Reflection API | Обход защиты |

---

## Лучшие практики безопасности

### 1. Минимальные права

Используйте минимальный набор разрешенных пакетов:

```yaml
security:
  allowed-packages:
    - org.bukkit
    - java.util
    - java.lang
```

### 2. Проверка скриптов

Всегда проверяйте скрипты перед добавлением на сервер:

- Ищите вызовы `java.lang.Runtime`
- Проверяйте сетевые запросы
- Следите за работой с файлами

### 3. Изоляция модулей

Используйте модули для изоляции функциональности разных скриптов.

### 4. Регулярные обновления

Обновляйте ScriptsLab для получения последних патчей безопасности.

---

## Безопасность на публичном сервере

### Рекомендуемая конфигурация

```yaml
security:
  sandbox-enabled: true
  allowed-packages:
    - org.bukkit
    - org.bukkit.entity
    - net.kyori
    - java.util
    - java.lang
  blocked-operations:
    - file-write
    - file-delete
    - system-exec
    - network-access
    - reflection
```

### Чек-лист

- [ ] Включена песочница
- [ ] Ограничены пакеты
- [ ] Заблокированы опасные операции
- [ ] Скрипты проверены
- [ ] Права игроков ограничены

---

## Troubleshooting

### Скрипт не работает в песочнице

**Проблема**: Скрипт выдает ошибку доступа.

**Решение**:
1. Проверьте, не использует ли скрипт заблокированные пакеты
2. Добавьте нужный пакет в `allowed-packages`
3. Или отключите песочницу (не рекомендуется)

### "Access denied" ошибка

**Проблема**: `java.security.AccessControlException`

**Решение**: Добавьте нужный пакет в конфигурацию:

```yaml
security:
  allowed-packages:
    - needed.package
```

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Конфигурация](configuration.md) | Настройка безопасности |
| [Модули](modules.md) | Изоляция кода |
| [FAQ](faq.md) | Частые вопросы |

---

# 🔒 ScriptsLab Security

Security guidelines for using ScriptsLab.

---

## Sandbox

Sandbox mode restricts scripts' access to dangerous Java functions and system operations.

### Enabling Sandbox

```yaml
# config.yml
security:
  sandbox-enabled: true
```

| Value | Security Level | Recommendation |
|-----------|----------------------|----------------|
| `false` | Low | Private servers, trusted scripts |
| `true` | High | Public servers, unverified scripts |

> **Important**: On public servers, it is strongly recommended to use `sandbox-enabled: true`!

---

## Allowed Packages

In sandbox mode, scripts can only use specified Java packages.

### Configuration

```yaml
security:
  allowed-packages:
    - org.bukkit
    - org.bukkit.entity
    - net.kyori
    - java.util
    - java.lang
    - java.util.concurrent
```

| Package | Description |
|--------|----------|
| `org.bukkit` | Core Bukkit API |
| `org.bukkit.entity` | Entities (players, mobs) |
| `net.kyori` | MiniMessage and components |
| `java.util` | Java utilities (List, Map) |
| `java.lang` | Base classes |

---

## Blocked Operations

List of operations prohibited in the sandbox:

```yaml
security:
  blocked-operations:
    - file-write
    - file-delete
    - system-exec
    - network-access
    - reflection
```

| Operation | Description | Risk |
|---------|-----------|------|
| `file-write` | Writing to files | System modification |
| `file-delete` | Deleting files | Data loss |
| `system-exec` | Executing system commands | RCE vulnerability |
| `network-access` | Network requests | Data leakage |
| `reflection` | Java Reflection API | Bypass protection |

---

## Security Best Practices

### 1. Minimal Permissions

Use a minimal set of allowed packages:

```yaml
security:
  allowed-packages:
    - org.bukkit
    - java.util
    - java.lang
```

### 2. Script Verification

Always verify scripts before adding them to the server:

- Look for `java.lang.Runtime` calls
- Check network requests
- Monitor file operations

### 3. Module Isolation

Use modules to isolate functionality of different scripts.

### 4. Regular Updates

Update ScriptsLab to get the latest security patches.

---

## Public Server Security

### Recommended Configuration

```yaml
security:
  sandbox-enabled: true
  allowed-packages:
    - org.bukkit
    - org.bukkit.entity
    - net.kyori
    - java.util
    - java.lang
  blocked-operations:
    - file-write
    - file-delete
    - system-exec
    - network-access
    - reflection
```

### Checklist

- [ ] Sandbox enabled
- [ ] Packages restricted
- [ ] Dangerous operations blocked
- [ ] Scripts verified
- [ ] Player permissions limited

---

## Troubleshooting

### Script not working in sandbox

**Problem**: Script throws access error.

**Solution**:
1. Check if the script uses blocked packages
2. Add the required package to `allowed-packages`
3. Or disable sandbox (not recommended)

### "Access denied" error

**Problem**: `java.security.AccessControlException`

**Solution**: Add the needed package to configuration:

```yaml
security:
  allowed-packages:
    - needed.package
```

---

## Next Steps

| Step | Description |
|-----|----------|
| [Configuration](configuration.md) | Security setup |
| [Modules](modules.md) | Code isolation |
| [FAQ](faq.md) | Frequently asked questions |
