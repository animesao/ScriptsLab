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