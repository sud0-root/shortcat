# 🐈 ShortCAT

**ShortCAT** — это мощный, кроссплатформенный текстовый автозаменитель (Text Expander) на Java.
Приложение работает глобально в системе, перехватывая короткие команды и заменяя их на заготовленные фразы.

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![License](https://img.shields.io/badge/License-MIT-blue)
![Platform](https://img.shields.io/badge/Platform-Win%20%7C%20Mac%20%7C%20Linux-lightgrey)

## ✨ Возможности

- 🚀 **Глобальный перехват:** Работает в любом приложении (Браузер, Word, Slack).
- 🎨 **Современный UI:** Интерфейс на базе FlatLaf (Solarized Pastel).
- ⚡ **Мгновенная работа:** Использует буфер обмена для быстрой вставки.
- 🛡️ **Безопасность:** Умное управление буфером и защита от рекурсии.
- 🖱️ **UX:** Сброс контекста при клике мышью или навигации.

## 🛠️ Сборка и Запуск

### Требования
- JDK 17 или выше
- Maven

### Установка

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/your-username/shortcat.git
   ```
2. Соберите проект:
   ```bash
   mvn clean package
   ```
3. Запустите JAR из папки `target/`:
   ```bash
   java -jar target/shortcat-1.0.0.jar
   ```

## 📦 Создание инсталлятора (jpackage)

Для создания `.exe` (Windows) или `.dmg` (macOS):

```bash
jpackage --input target/ --name ShortCAT --main-jar shortcat-1.0.0.jar ...
```

## 📄 Лицензия

Этот проект распространяется под лицензией MIT.
```