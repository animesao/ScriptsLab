# ⚙️ Настройка ScriptsLab

Полное руководство по конфигурации плагина ScriptsLab. Здесь описаны все параметры файла `config.yml`.

---

## Структура файла конфигурации

Файл `config.yml` находится в папке `plugins/ScriptsLab/config.yml`:

```yaml
# ScriptsLab Configuration
# Production-grade scriptable plugin framework

# Общие настройки
general:
  language: en
  debug: false
  auto-save-interval: 5

# Настройки скриптового движка
scripts:
  enabled: true
  auto-reload: true
  timeout: 5000
  max-memory: 128

# Настройки модулей
modules:
  auto-load: true
  enabled-modules:
    - demo
  disabled-modules: []

# Настройки хранилища
storage:
  provider: yaml
  cache-enabled: true
  cache-size: 1000
  auto-save: true

# Настройки производительности
performance:
  metrics-enabled: true
  async-pool-size: 4
  event-threads: 2

# Настройки безопасности
security:
  sandbox-enabled: false
  allowed-packages:
    - org.bukkit
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

---

## Общие настройки (general)

### language

**Описание**: Язык сообщений плагина

**Тип**: `string` (en, ru)

**По умолчанию**: `en`

**Пример**:
```yaml
general:
  language: ru  # Русский язык
```

| Значение | Язык |
|----------|------|
| `en` | Английский |
| `ru` | Русский |

---

### debug

**Описание**: Включить режим отладки (более подробное логирование)

**Тип**: `boolean`

**По умолчанию**: `false`

**Пример**:
```yaml
general:
  debug: true  # Включить отладку
```

| Значение | Описание |
|----------|-----------|
| `false` | Только важные сообщения |
| `true` | Все сообщения включая отладочные |

---

### auto-save-interval

**Описание**: Интервал автоматического сохранения данных (в минутах)

**Тип**: `integer` (минуты)

**По умолчанию**: `5`

**Пример**:
```yaml
general:
  auto-save-interval: 10  # Сохранять каждые 10 минут
```

---

## Настройки скриптов (scripts)

### enabled

**Описание**: Включить скриптовый движок

**Тип**: `boolean`

**По умолчанию**: `true`

**Пример**:
```yaml
scripts:
  enabled: false  # Отключить скрипты (не рекомендуется)
```

---

### auto-reload

**Описание**: Автоматическая перезагрузка скриптов при изменении файла

**Тип**: `boolean`

**По умолчанию**: `true`

**Пример**:
```yaml
scripts:
  auto-reload: true  # Перезагружать при изменении
```

> **Важно**: При `true` скрипты автоматически перезагружаются при сохранении файла. Это удобно для разработки, но может вызвать проблемы на продакшн серверах.

---

### timeout

**Описание**: Таймаут выполнения скри��та (в миллисекундах)

**Тип**: `integer` (мс)

**По умолчанию**: `5000` (5 секунд)

**Пример**:
```yaml
scripts:
  timeout: 10000  # 10 секунд на выполнение
```

| Значение | Время | Рекомендация |
|----------|-------|-------------|
| 1000 | 1 сек | Очень быстрые скрипты |
| 5000 | 5 сек | По умолчанию |
| 30000 | 30 сек | Сложные операции |

---

### max-memory

**Описание**: Максимальная память на скрипт (MB)

**Тип**: `integer`

**По умолчанию**: `128`

**Пример**:
```yaml
scripts:
  max-memory: 256  # 256MB на скрипт
```

---

## Настройки модулей (modules)

### auto-load

**Описание**: Автоматическая загрузка модулей при старте

**Тип**: `boolean`

**По умолчанию**: `true`

**Пример**:
```yaml
modules:
  auto-load: true
```

---

### enabled-modules

**Описание**: Список модулей для загрузки (пустой = загрузить все)

**Тип**: `list`

**По умолчанию**: `[]` (все модули)

**Пример**:
```yaml
modules:
  enabled-modules:
    - demo
    - economy
    - customitems
```

---

### disabled-modules

**Описание**: Список модулей для отключения

**Тип**: `list`

**По умолчанию**: `[]`

**Пример**:
```yaml
modules:
  disabled-modules:
    - debug
    - testmodule
```

---

## Настройки хранилища (storage)

### provider

**Описание**: Тип хранилища данных

**Тип**: `string` (yaml, json, sqlite)

**По умолчанию**: `yaml`

**Пример**:
```yaml
storage:
  provider: sqlite  # Использовать SQLite
```

| Значение | Описание |
|----------|-----------|
| `yaml` | Файлы YAML (по умолчанию) |
| `json` | Файлы JSON |
| `sqlite` | База данных SQLite |

---

### cache-enabled

**Описание**: Включить кэширование данных

**Тип**: `boolean`

**По умолчанию**: `true`

**Пример**:
```yaml
storage:
  cache-enabled: false  # Отключить кэш
```

---

### cache-size

**Описание**: Размер кэша (количество записей)

**Тип**: `integer`

**По умолчанию**: `1000`

**Пример**:
```yaml
storage:
  cache-size: 5000  # Большой кэш
```

---

### auto-save

**Описание**: Автоматическое сохранение при выключении сервера

**Тип**: `boolean`

**По умолчанию**: `true`

**Пример**:
```yaml
storage:
  auto-save: false
```

---

## Настройки производительности (performance)

### metrics-enabled

**Описание**: Включить сбор метрик производительности

**Тип**: `boolean`

**По умолчанию**: `true`

**Пример**:
```yaml
performance:
  metrics-enabled: false  # Отключить метрики
```

---

### async-pool-size

**Описание**: Размер пула асинхронных задач

**Тип**: `integer`

**По умолчанию**: `4`

**Пример**:
```yaml
performance:
  async-pool-size: 8  # Больше асинхронных задач
```

---

### event-threads

**Описание**: Количество потоков для обработки событий

**Тип**: `integer`

**По умолчанию**: `2`

**Пример**:
```yaml
performance:
  event-threads: 4  # Больше потоков для событий
```

---

## Настройки безопасности (security)

### ⚠️ sandbox-enabled

**Описание**: Включить песочницу (sandbox) для скриптов

**Тип**: `boolean`

**По умолчанию**: `false`

**Пример**:
```yaml
security:
  sandbox-enabled: true  # Включить защиту
```

| Значение | Уровень безопасности | Использование |
|-----------|----------------------|----------------|
| `false` | Низкий | Приватные серверы |
| `true` | Высокий | Публичные серверы |

> **Важно**: На публичных серверах настоятельно рекомендуется использовать `sandbox-enabled: true`!

---

### allowed-packages

**Описание**: Разрешённые Java пакеты в режиме песочницы

**Тип**: `list`

**По умолчанию**:
```yaml
allowed-packages:
  - org.bukkit
  - net.kyori
  - java.util
  - java.lang
```

**Пример**:
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

---

### blocked-operations

**Описание**: Заблокированные операции в песочнице

**Тип**: `list`

**По умолчанию**:
```yaml
blocked-operations:
  - file-write
  - file-delete
  - system-exec
  - network-access
  - reflection
```

| Операция | Описание |
|---------|-----------|
| `file-write` | Запись в файлы |
| `file-delete` | Удаление файлов |
| `system-exec` | Выполнение системных команд |
| `network-access` | Сетевые запросы |
| `reflection` | Java Reflection API |

---

## Примеры конфигураций

### Конфигурация для приватного сервера

```yaml
general:
  language: ru
  debug: false
  auto-save-interval: 5

scripts:
  enabled: true
  auto-reload: true
  timeout: 10000
  max-memory: 256

modules:
  auto-load: true
  enabled-modules: []
  disabled-modules: []

storage:
  provider: yaml
  cache-enabled: true
  cache-size: 5000
  auto-save: true

performance:
  metrics-enabled: true
  async-pool-size: 4
  event-threads: 2

security:
  sandbox-enabled: false
  allowed-packages:
    - org.bukkit
    - net.kyori
    - java.util
    - java.lang
  blocked-operations: []
```

### Конфигурация для публичного сервера

```yaml
general:
  language: en
  debug: false
  auto-save-interval: 3

scripts:
  enabled: true
  auto-reload: false
  timeout: 5000
  max-memory: 128

modules:
  auto-load: true
  enabled-modules: []
  disabled-modules: []

storage:
  provider: yaml
  cache-enabled: true
  cache-size: 2000
  auto-save: true

performance:
  metrics-enabled: true
  async-pool-size: 8
  event-threads: 4

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

### Конфигурация для разработки

```yaml
general:
  language: en
  debug: true
  auto-save-interval: 1

scripts:
  enabled: true
  auto-reload: true
  timeout: 30000
  max-memory: 512

modules:
  auto-load: true
  enabled-modules:
    - demo
  disabled-modules: []

storage:
  provider: yaml
  cache-enabled: false
  cache-size: 100
  auto-save: true

performance:
  metrics-enabled: true
  async-pool-size: 2
  event-threads: 1

security:
  sandbox-enabled: false
  allowed-packages:
    - org.bukkit
    - net.kyori
    - java.util
    - java.lang
  blocked-operations: []
```

---

## Перезагрузка конфигурации

После изменения `config.yml` перезагрузите плагин:

```
/scriptslab reload
```

Или перезапустите сервер.

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Команды](commands.md) | Управление плагином |
| [Права](permissions.md) | Настройка прав доступа |
| [Script API](script-api.md) | API для скриптов |

---

# ⚙️ ScriptsLab Configuration (English)

Complete guide to configuring ScriptsLab plugin. All `config.yml` parameters are described here.

---

## Configuration File Structure

The `config.yml` file is located at `plugins/ScriptsLab/config.yml`:

```yaml
# ScriptsLab Configuration
# Production-grade scriptable plugin framework

# General settings
general:
  language: en
  debug: false
  auto-save-interval: 5

# Script engine settings
scripts:
  enabled: true
  auto-reload: true
  timeout: 5000
  max-memory: 128

# Module settings
modules:
  auto-load: true
  enabled-modules:
    - demo
  disabled-modules: []

# Storage settings
storage:
  provider: yaml
  cache-enabled: true
  cache-size: 1000
  auto-save: true

# Performance settings
performance:
  metrics-enabled: true
  async-pool-size: 4
  event-threads: 2

# Security settings
security:
  sandbox-enabled: false
  allowed-packages:
    - org.bukkit
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

---

## General Settings (general)

### language

**Description**: Plugin message language

**Type**: `string` (en, ru)

**Default**: `en`

**Example**:
```yaml
general:
  language: ru  # Russian language
```

| Value | Language |
|----------|------|
| `en` | English |
| `ru` | Russian |

---

### debug

**Description**: Enable debug mode (verbose logging)

**Type**: `boolean`

**Default**: `false`

**Example**:
```yaml
general:
  debug: true  # Enable debugging
```

| Value | Description |
|----------|-----------|
| `false` | Important messages only |
| `true` | All messages including debug |

---

### auto-save-interval

**Description**: Data auto-save interval (in minutes)

**Type**: `integer` (minutes)

**Default**: `5`

**Example**:
```yaml
general:
  auto-save-interval: 10  # Save every 10 minutes
```

---

## Script Settings (scripts)

### enabled

**Description**: Enable script engine

**Type**: `boolean`

**Default**: `true`

**Example**:
```yaml
scripts:
  enabled: false  # Disable scripts (not recommended)
```

---

### auto-reload

**Description**: Auto-reload scripts when file changes

**Type**: `boolean`

**Default**: `true`

**Example**:
```yaml
scripts:
  auto-reload: true  # Reload on change
```

> **Important**: When `true`, scripts auto-reload on save. Convenient for development but may cause issues on production servers.

---

### timeout

**Description**: Script execution timeout (in milliseconds)

**Type**: `integer` (ms)

**Default**: `5000` (5 seconds)

**Example**:
```yaml
scripts:
  timeout: 10000  # 10 seconds to execute
```

| Value | Time | Recommendation |
|----------|-------|-------------|
| 1000 | 1 sec | Very fast scripts |
| 5000 | 5 sec | Default |
| 30000 | 30 sec | Complex operations |

---

### max-memory

**Description**: Maximum memory per script (MB)

**Type**: `integer`

**Default**: `128`

**Example**:
```yaml
scripts:
  max-memory: 256  # 256MB per script
```

---

## Module Settings (modules)

### auto-load

**Description**: Auto-load modules on startup

**Type**: `boolean`

**Default**: `true`

**Example**:
```yaml
modules:
  auto-load: true
```

---

### enabled-modules

**Description**: List of modules to load (empty = load all)

**Type**: `list`

**Default**: `[]` (all modules)

**Example**:
```yaml
modules:
  enabled-modules:
    - demo
    - economy
    - customitems
```

---

### disabled-modules

**Description**: List of modules to disable

**Type**: `list`

**Default**: `[]`

**Example**:
```yaml
modules:
  disabled-modules:
    - debug
    - testmodule
```

---

## Storage Settings (storage)

### provider

**Description**: Data storage type

**Type**: `string` (yaml, json, sqlite)

**Default**: `yaml`

**Example**:
```yaml
storage:
  provider: sqlite  # Use SQLite
```

| Value | Description |
|----------|-----------|
| `yaml` | YAML files (default) |
| `json` | JSON files |
| `sqlite` | SQLite database |

---

### cache-enabled

**Description**: Enable data caching

**Type**: `boolean`

**Default**: `true`

**Example**:
```yaml
storage:
  cache-enabled: false  # Disable cache
```

---

### cache-size

**Description**: Cache size (number of entries)

**Type**: `integer`

**Default**: `1000`

**Example**:
```yaml
storage:
  cache-size: 5000  # Large cache
```

---

### auto-save

**Description**: Auto-save on server shutdown

**Type**: `boolean`

**Default**: `true`

**Example**:
```yaml
storage:
  auto-save: false
```

---

## Performance Settings (performance)

### metrics-enabled

**Description**: Enable performance metrics collection

**Type**: `boolean`

**Default**: `true`

**Example**:
```yaml
performance:
  metrics-enabled: false  # Disable metrics
```

---

### async-pool-size

**Description**: Async task pool size

**Type**: `integer`

**Default**: `4`

**Example**:
```yaml
performance:
  async-pool-size: 8  # More async tasks
```

---

### event-threads

**Description**: Number of threads for event processing

**Type**: `integer`

**Default**: `2`

**Example**:
```yaml
performance:
  event-threads: 4  # More event threads
```

---

## Security Settings (security)

### ⚠️ sandbox-enabled

**Description**: Enable script sandbox

**Type**: `boolean`

**Default**: `false`

**Example**:
```yaml
security:
  sandbox-enabled: true  # Enable protection
```

| Value | Security Level | Usage |
|-----------|----------------------|----------------|
| `false` | Low | Private servers |
| `true` | High | Public servers |

> **Important**: On public servers, it is strongly recommended to use `sandbox-enabled: true`!

---

### allowed-packages

**Description**: Allowed Java packages in sandbox mode

**Type**: `list`

**Default**:
```yaml
allowed-packages:
  - org.bukkit
  - net.kyori
  - java.util
  - java.lang
```

**Example**:
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

---

### blocked-operations

**Description**: Blocked operations in sandbox

**Type**: `list`

**Default**:
```yaml
blocked-operations:
  - file-write
  - file-delete
  - system-exec
  - network-access
  - reflection
```

| Operation | Description |
|---------|-----------|
| `file-write` | Write to files |
| `file-delete` | Delete files |
| `system-exec` | Execute system commands |
| `network-access` | Network requests |
| `reflection` | Java Reflection API |

---

## Configuration Examples

### Private Server Configuration

```yaml
general:
  language: ru
  debug: false
  auto-save-interval: 5

scripts:
  enabled: true
  auto-reload: true
  timeout: 10000
  max-memory: 256

modules:
  auto-load: true
  enabled-modules: []
  disabled-modules: []

storage:
  provider: yaml
  cache-enabled: true
  cache-size: 5000
  auto-save: true

performance:
  metrics-enabled: true
  async-pool-size: 4
  event-threads: 2

security:
  sandbox-enabled: false
  allowed-packages:
    - org.bukkit
    - net.kyori
    - java.util
    - java.lang
  blocked-operations: []
```

### Public Server Configuration

```yaml
general:
  language: en
  debug: false
  auto-save-interval: 3

scripts:
  enabled: true
  auto-reload: false
  timeout: 5000
  max-memory: 128

modules:
  auto-load: true
  enabled-modules: []
  disabled-modules: []

storage:
  provider: yaml
  cache-enabled: true
  cache-size: 2000
  auto-save: true

performance:
  metrics-enabled: true
  async-pool-size: 8
  event-threads: 4

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

### Development Configuration

```yaml
general:
  language: en
  debug: true
  auto-save-interval: 1

scripts:
  enabled: true
  auto-reload: true
  timeout: 30000
  max-memory: 512

modules:
  auto-load: true
  enabled-modules:
    - demo
  disabled-modules: []

storage:
  provider: yaml
  cache-enabled: false
  cache-size: 100
  auto-save: true

performance:
  metrics-enabled: true
  async-pool-size: 2
  event-threads: 1

security:
  sandbox-enabled: false
  allowed-packages:
    - org.bukkit
    - net.kyori
    - java.util
    - java.lang
  blocked-operations: []
```

---

## Reloading Configuration

After changing `config.yml`, reload the plugin:

```
/scriptslab reload
```

Or restart the server.

---

## Next Steps

| Step | Description |
|-----|----------|
| [Commands](commands.md) | Plugin management |
| [Permissions](permissions.md) | Access rights setup |
| [Script API](script-api.md) | API for scripts |