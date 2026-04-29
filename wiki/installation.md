# 📥 Установка ScriptsLab

Полное руководство по установке плагина ScriptsLab на ваш Minecraft сервер.

---

## Требования

### Системные требования

| Требование | Минимум | Рекомендуется |
|-------------|---------|---------------|
| Java | 17 | 17 LTS или 21 |
| RAM | 2GB | 4GB+ (с GraalVM) |
| Дисковое пространство | 100MB | 200MB |
| CPU | 1 ядро | 2+ ядра |

### Серверные требования

| Требование | Версия |
|------------|--------|
| Minecraft Server | Paper 1.20.4+ |
| Fork | Paper / Spigot / Pufferfish |
| Версия протокола | 1.20.4 (762) или выше |

> **Важно**: ScriptsLab работает только на Paper или совместимых форках (Spigot, Pufferfish). На чистом CraftBukkit плагин не будет работать!

---

## Установка за 5 минут

### Шаг 1: Скачайте плагин

Скачайте последнюю версию плагина:
- **GitHub Releases**: [scriptslab/releases](https://github.com/animesao/scriptslab/releases)
- Или соберите из исходного кода (см. ниже)

### Шаг 2: Установите на сервер

1. Остановите сервер (если запущен)
2. Скопируйте файл `ScriptsLab-1.0.0.jar` в папку `plugins/`
3. Запустите сервер

```bash
# Пример для Linux/macOS
cp ScriptsLab-1.0.0.jar /path/to/server/plugins/

# Пример для Windows
copy ScriptsLab-1.0.0.jar C:\server\plugins\
```

### Шаг 3: Проверьте установку

При запуске сервера вы должны увидеть:

```
[ScriptsLab] ✓ ScriptsLab v1.0.0 loaded
[ScriptsLab] ✓ Script Engine initialized (GraalVM)
[ScriptsLab] ✓ Loaded X scripts
[ScriptsLab] ✓ Enabled!
```

### Шаг 4: Настройте права (опционально)

Добавьте права в `permissions.yml`:

```yaml
permissions:
  scriptslab.*:
    children:
      scriptslab.use: true
      scriptslab.reload: true
      scriptslab.module: true
      scriptslab.script: true
      scriptslab.admin: true
```

---

## Сборка из исходного кода

### Требования для сборки

| Инструмент | Версия |
|------------|--------|
| Java JDK | 17 |
| Maven | 3.8+ |
| Git | Любая |

### Инструкция по сборке

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/animesao/scriptslab.git
cd ScriptsLab

# 2. Соберите плагин
mvn clean package -DskipTests

# 3. Найдите готовый JAR
ls -la target/ScriptsLab-1.0.0.jar
```

### Альтернативная сборка (только JAR)

```bash
# Сборка без тестов (быстрее)
mvn clean package -DskipTests -Dmaven.test.skip=true
```

### Структура после сборки

```
target/
├── ScriptsLab-1.0.0.jar    # Готовый плагин (~50MB с GraalVM)
├── classes/                # Скомпилированные классы
└── generated-sources/     # Сгенерированные исходники
```

---

## Первый запуск

### Что происходит при первом запуске?

1. Создаётся папка `plugins/ScriptsLab/`
2. Генерируется `config.yml` с настройками по умолчанию
3. Создаётся `messages.yml` с сооб��ениями
4. Создаётся папка `scripts/` для ваших скриптов
5. Создаётся папка `modules/` для модулей

### Структура после первого запуска

```
plugins/ScriptsLab/
├── config.yml              # Конфигурация плагина
├── messages.yml          # Сообщения
├── scripts/             # Ваши скрипты
│   ├── examples/       # Примеры скриптов
│   └── readme.txt     # Инструкция
├── modules/           # Модули
│   └── demo/
│       └── module.yml
└── storage/          # Сохранённые данные
```

---

## Настройка Java

### Проверка версии Java

```bash
# Linux/macOS
java -version

# Windows
java -version
```

Ожидаемый вывод:
```
java version "17.0.x"
Java(TM) SE Runtime Environment (build 17.0.x+...)
```

### Установка Java 17 (Linux)

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# Arch Linux
sudo pacman -S jdk17-openjdk
```

### Установка Java 17 (Windows)

1. Скачайте JDK 17 с [Adoptium](https://adoptium.net/)
2. Запустите установщик
3. Установите переменную JAVA_HOME:

```cmd
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.0.x" /M
```

### Настройка запуска сервера

Добавьте в startup скрипт:

```bash
# Linux - start.sh
#!/bin/bash
java -Xmx4G -Xms2G -jar paper.jar --nogui

# Windows - start.bat
@echo off
java -Xmx4G -Xms2G -jar paper.jar --nogui
pause
```

| Параметр | Описание |
|----------|-----------|
| -Xmx4G | МаксимальнаяRAM (4GB) |
| -Xms2G | Минимальная RAM (2GB) |
| --nogui | Без GUI (для сервера) |

---

## Удаление плагина

### Безопасное удаление

1. Остановите сервер
2. Удалите файл `plugins/ScriptsLab-*.jar`
3. Удалите папку `plugins/ScriptsLab/` (если нужны данные, сделайте backup)

```bash
# Linux
rm plugins/ScriptsLab-1.0.0.jar
# Или оставить для backup данных
mv plugins/ScriptsLab/ plugins/ScriptsLab_backup/

# Windows
del plugins\ScriptsLab-1.0.0.jar
```

### Что удалить при полном удалении

```
plugins/
├── ScriptsLab-1.0.0.jar    # ❌ Удалить
└── ScriptsLab/              # ❌ Удалить (все данные)
    ├── config.yml
    ├── messages.yml
    ├── scripts/
    ├── modules/
    └── storage/
```

---

## Частые проблемы при установке

### "Java version not supported"

**Проблема**: Сервер не запускается с ошибкой о версии Java.

**Решение**:
```bash
# Проверьте версию Java
java -version

# Убедитесь, что используется Java 17+
# Измените JAVA_HOME на путь к Java 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### "Plugin does not support this server type"

**Проблема**: Плагин несовместим с вашим сервером.

**Решение**:
- Убедитесь, что используете Paper, Spigot или Pufferfish
- Проверьте версию сервера (должна быть 1.20.4+)
- Не используйте CraftBukkit!

### "OutOfMemoryError"

**Проблема**: Не хватает оперативной памяти.

**Решение**:
- Увеличьте выделенную память в startup скрипте
- Рекомендуется минимум 3GB для сервера с GraalVM

---

## Следующие шаги

| Шаг | Описание |
|-----|----------|
| [Настройка](configuration.md) | Настройте плагин под себя |
| [Команды](commands.md) | Изучите доступные команды |
| [Script API](script-api.md) | Начните писать скрипты |
| [Примеры](examples/) | Посмотрите готовые примеры |

---

## Поддержка

Если у вас возникли проблемы при установке:
- **GitHub Issues**: [Сообщить об ошибке](https://github.com/scriptslab/scriptslab/issues)
- **FAQ**: [Частые вопросы](faq.md)
