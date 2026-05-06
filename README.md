# ЖД Билеты — Android App (Java)

Лабораторная работа №1 по дисциплине «Разработка ПО для мобильных платформ».  
Тема: **Приложение для поиска билетов на поезда**.

---

## Требования к среде разработки

| Инструмент        | Версия              |
|-------------------|---------------------|
| Android Studio    | Hedgehog 2023.1.1+  |
| JDK               | 11 или 17           |
| Gradle            | 8.2                 |
| compileSdk        | 34 (Android 14)     |
| minSdk            | 24 (Android 7.0)    |

---

## Запуск проекта

1. Открыть Android Studio → **File → Open** → выбрать папку `TrainTickets/`
2. Дождаться синхронизации Gradle (первый запуск может занять несколько минут)
3. Подключить физическое устройство (USB Debugging) или запустить эмулятор (AVD)
4. Нажать **Run ▶** (Shift+F10)

---

## Структура проекта

```
TrainTickets/
├── app/
│   ├── src/main/
│   │   ├── java/com/traintickets/
│   │   │   ├── activities/
│   │   │   │   ├── BaseActivity.java       ← тема + локаль
│   │   │   │   ├── SplashActivity.java     ← сплеш-экран
│   │   │   │   ├── MainActivity.java       ← поиск маршрута
│   │   │   │   ├── ResultsActivity.java    ← список поездов
│   │   │   │   ├── DetailsActivity.java    ← детали рейса
│   │   │   │   └── SettingsActivity.java   ← настройки
│   │   │   ├── adapters/
│   │   │   │   ├── TrainAdapter.java        ← RecyclerView поездов
│   │   │   │   └── SavedTicketAdapter.java  ← RecyclerView сохранённых
│   │   │   ├── database/
│   │   │   │   └── DBHelper.java           ← SQLite (CRUD)
│   │   │   ├── models/
│   │   │   │   ├── Train.java              ← модель поезда
│   │   │   │   └── SavedTicket.java        ← модель сохранённого билета
│   │   │   └── utils/
│   │   │       ├── PrefsManager.java        ← SharedPreferences (тема/язык)
│   │   │       ├── LocaleHelper.java        ← программная смена языка
│   │   │       └── TrainDataGenerator.java  ← тестовые данные
│   │   └── res/
│   │       ├── layout/                     ← XML-лейауты экранов
│   │       ├── values/                     ← строки RU, цвета, темы
│   │       ├── values-en/                  ← строки EN
│   │       ├── values-night/               ← цвета для тёмной темы
│   │       ├── anim/                       ← анимации (fade_in, slide_up)
│   │       └── drawable/                   ← иконки (SVG векторные)
```

---

## Выполненные требования ЛР №1

| № | Требование                          | Реализация                                                        |
|---|-------------------------------------|-------------------------------------------------------------------|
| 1 | Минимум 3 экрана                    | 5 экранов: Splash, Main, Results, Details, Settings               |
| 2 | Сплеш-экран                         | `SplashActivity` с анимацией `fade_in` + `slide_up`              |
| 3 | Локализация (RU + EN)               | `res/values/strings.xml` + `res/values-en/strings.xml`           |
| 4 | Светлая/тёмная тема с сохранением   | `AppCompatDelegate` + `SharedPreferences` в `PrefsManager`       |
| 5 | Локальная БД (создание/удаление/ред)| `DBHelper.java` (SQLite): поля `route`, `date`, `price`          |

---

## Экраны приложения

### 1. SplashActivity
Отображается при запуске. Показывает логотип и название приложения с анимацией.  
Через 2.5 секунды автоматически переходит на `MainActivity`.

### 2. MainActivity — Поиск маршрута
Поля: **Откуда**, **Куда**, **Дата отправления** (DatePickerDialog), **Пассажиры**.  
Кнопка ⇄ меняет местами Откуда и Куда.  
Валидация: все поля обязательны, откуда ≠ куда.

### 3. ResultsActivity — Список поездов
Отображает поезда в `RecyclerView` с карточками:  
номер поезда, тип, время отправления/прибытия, длительность, минимальная цена.

### 4. DetailsActivity — Детали рейса
Полная информация о рейсе + цены по типам вагонов (плацкарт, купе, СВ).  
Кнопка **«Сохранить билет»** добавляет запись в SQLite.

### 5. SettingsActivity — Настройки
- Переключатель темы (светлая/тёмная) — применяется сразу через `recreate()`
- Переключатель языка (RU/EN) — перезапускает `MainActivity` с новой локалью
- Список сохранённых билетов с возможностью редактировать и удалять

---

## База данных SQLite

**Таблица:** `saved_tickets`

| Поле   | Тип     | Описание                              |
|--------|---------|---------------------------------------|
| `id`   | INTEGER | PRIMARY KEY AUTOINCREMENT             |
| `route`| TEXT    | Маршрут: "Москва → СПб (Поезд 001А)" |
| `date` | TEXT    | Дата поездки: "2026-05-01"            |
| `price`| INTEGER | Цена в рублях                         |

Операции: `INSERT`, `SELECT`, `UPDATE`, `DELETE`.

---

## Зависимости (app/build.gradle)

```groovy
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.cardview:cardview:1.0.0'
```
