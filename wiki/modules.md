# 🔌 Система модулей ScriptsLab

Полное руководство по системе модулей плагина ScriptsLab.

---

## Что такое модули?

Модули в ScriptsLab - это переиспользуемые пакеты функциональности, которые могут быть загружены или отключены независимо друг от друга.

### Преимущества модулей

| Преимущество | Описание |
|--------------|----------|
| 🔄 **Горячая загрузка** | Включение/отключение без перезапуска сервера |
| 📦 **Переиспользуемость** | Один модуль может использоваться на разных серверах |
| 🔒 **Изоляция** | Модули не зависят друг от друга (если не указано иное) |
| 🛠️ **Удобство разработки** | Простая структура и API |

---

## Структура модуля

```
modules/
└── mymodule/
    ├── module.yml       # Конфигурация модуля
    └── scripts/         # Скрипты модуля (опционально)
        └── script.js
```

### module.yml

```yaml
# Конфигурация модуля
id: mymodule
name: My Module
version: 1.0.0
description: My custom module
authors:
  - MyName

# Зависимости
dependencies: []
soft-dependencies: []

# Порядок загрузки: STARTUP, POSTWORLD, или LAZY
load-order: STARTUP

# Включён по умолчанию
enabled: true
```

---

## Параметры module.yml

### id

**Описание**: Уникальный идентификатор модуля

**Тип**: `string`

**Пример**: `id: economy`

---

### name

**Описание**: Отображаемое имя модуля

**Тип**: `string`

**Пример**: `name: Economy System`

---

### version

**Описание**: Версия модуля

**Тип**: `string`

**Пример**: `version: 1.0.0`

---

### description

**Описание**: Описание модуля

**Тип**: `string`

**Пример**: `description: Система экономики`

---

### authors

**Описание**: Авторы модуля

**Тип**: `list`

**Пример**:
```yaml
authors:
  - MyName
  - OtherAuthor
```

---

### dependencies

**Описок модулей, которые должны быть загружены перед этим модулем**

**Тип**: `list`

**Пример**:
```yaml
dependencies:
  - economy
  - customitems
```

---

### soft-dependencies

**Описание**: Модули, которые рекомендуется загрузить

**Тип**: `list`

**Пример**:
```yaml
soft-dependencies:
  - tokens
```

---

### load-order

**Описание**: Порядок загрузки модуля

**Тип**: `string`

| Значение | Описание |
|----------|-----------|
| `STARTUP` | Загружается при старте |
| `POSTWORLD` | Загружается после мира |
| `LAZY` | Загружается при первом использовании |

**Пример**:
```yaml
load-order: STARTUP
```

---

### enabled

**Описание**: Включён ли модуль по умолчанию

**Тип**: `boolean`

**Пример**:
```yaml
enabled: true
```

---

## Управление модулями

### Команды управления

| Команда | Описание |
|---------|----------|
| `/module list` | Список всех модулей |
| `/module enable <name>` | Включить модуль |
| `/module disable <name>` | Выключить модуль |
| `/module reload <name>` | Перезагрузить модуль |

---

## Пример модуля: Demo

Посмотрим на пример модуля demo:

**Файл**: `plugins/ScriptsLab/modules/demo/module.yml`

```yaml
id: demo
name: Demo Module
version: 1.0.0
description: Demonstration module showcasing framework capabilities
authors:
  - Framework Team

dependencies: []
soft-dependencies: []

load-order: STARTUP

enabled: true
```

---

## Создание своего модуля

### Шаг 1: Создайте папку

Создайте папку `plugins/ScriptsLab/modules/mymodule/`

### Шаг 2: Создайте module.yml

Создайте файл `module.yml`:

```yaml
id: mymodule
name: My Module
version: 1.0.0
description: Description of my module
authors:
  - YourName

load-order: STARTUP
enabled: true
```

### Шаг 3: Добавьте скрипты (опционально)

Создайте папку `scripts/` и добавьте скрипты.

### Шаг 4: Включите модуль

Добавьте модуль в `config.yml`:

```yaml
modules:
  enabled-modules:
    - mymodule
```

### Шаг 5: Перезагрузите

Выполните `/module reload` или перезапустите сервер.

---

## Конфигурация модулей в config.yml

### enabled-modules

**Описание**: Список модулей для загрузки

**Тип**: `list`

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

**Пример**:
```yaml
modules:
  disabled-modules:
    - debug
    - testmodule
```

---

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

## Зависимости между модулями

### Жёсткие зависимости (dependencies)

Модуль не загрузится, если зависимость не загружена:

```yaml
dependencies:
  - economy
  - tokens
```

### Мягкие зависимости (soft-dependencies)

Модуль загрузится, но может использовать функции зависимости:

```yaml
soft-dependencies:
  - chat
```

---

## Лучшие практики

### 1. Используйте понятные ID

```yaml
id: economy        # Хорошо
id: economic     # Плохо
id: e            # Плохо
```

### 2. Указывайте авторов

```yaml
authors:
  - YourName
```

### 3. Документируйте модуль

```yaml
description: |
  Модуль экономики.
  Предоставляет систему магазинов и валюты.
```

### 4. Версионируйте

```yaml
version: 1.0.0
```

---

## Troubleshooting

### Модуль не загружается

1. Проверьте синтаксис `module.yml`
2. Проверьте зависимости
3. Проверьте `config.yml`

### Ошибка зависимостей

```
[ScriptsLab] Module 'mymodule' requires 'othermodule' which is not loaded
```

**Решение**: Добавьте зависимый модуль в `enabled-modules`.

### Конфликт имён

```
[ScriptsLab] Module with ID 'mymodule' already exists
```

**Решение**: Измените ID модуля.

---

## Структура файлов модуля

```
mymodule/
├── module.yml           # Обязательно
├── scripts/             # Опционально
│   ├── main.js
│   ├── commands.js
│   └── events.js
├── config/              # Опционально
│   └── settings.yml
└── locale/              # Опционально
    └── messages.yml
```

---

## Примеры модулей

### Минимальный модуль

```
mymodule/
└── module.yml
```

```yaml
id: mymodule
name: My Module
version: 1.0.0
description: Example module
authors:
  - Author

enabled: true
```

### Полный модуль

```
mymodule/
├── module.yml
├── scripts/
│   ├── main.js
│   └── events.js
└── config/
    └── settings.yml
```

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Script API](script-api.md) | API для скриптов |
| [Конфигурация](configuration.md) | Настройка плагина |
| [Примеры](examples/) | Готовые примеры |