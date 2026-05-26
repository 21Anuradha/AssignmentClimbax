# Flipzon App — E-Commerce Android Assignment

**Flipzon** is a simple e-commerce feed app built with the **DummyJSON API**. It demonstrates **Kotlin**, **MVVM**, **Retrofit**, **Room**, and **Kotlin Coroutines & Flow**.

## Tech Stack

| Layer | Technology |
|--------|------------|
| Language | Kotlin |
| Architecture | MVVM + Repository pattern |
| UI | XML (ViewBinding), Material Design 3 |
| Networking | Retrofit + OkHttp + Gson |
| Session | DataStore Preferences |
| Local DB | Room (SQLite) for cart |
| Concurrency | Coroutines, Flow, LiveData |
| Images | Coil |
| DI | Manual (`AppContainer`) |

## API

Base URL: `https://dummyjson.com/`

- `POST /auth/login` — Login
- `GET /products?limit=&skip=` — Paginated product feed
- `GET /products/search?q=` — Search (400ms debounce)
- `POST /carts/add` — Checkout

**Test credentials:** `emilys` / `emilyspass`

## Features

### Authentication & session
- DummyJSON Login API
- DataStore: `id`, `email`, `firstName`, `lastName`, `image`
- Launch: **Home** if session exists, else **Login** (no back stack)
- Logout: clears session + Room cart → Login

### Top app bar
- Profile image, full name, email (from DataStore)
- Logout

### Home (product feed)
- Paginated RecyclerView
- Loading / Error / Empty + Retry
- Search with debounce
- Add to cart → Room

### Cart (offline-first)
- Room table `cart_items` in database `flipzon_db`
- Reactive Flow → LiveData
- Increment / decrement; remove at qty 0
- Checkout API; success clears cart, failure keeps cart

## Offline behaviour

| Screen | Internet OFF |
|--------|----------------|
| Home (products) | New feed does not load (API). Previously loaded list may show until refresh. |
| Cart | Works — items from Room |

## Test cart offline

1. Login with internet ON (`emilys` / `emilyspass`)
2. Add products from Home → open Cart tab
3. Enable **Airplane mode**
4. Cart tab still shows items; +/- works
5. Checkout needs internet again

**Database Inspector:** process `com.example.assignmentclimbax` → `flipzon_db` → `cart_items`

## Project structure

```
app/src/main/java/com/example/assignmentclimbax/
├── data/       # API, Room, DataStore, repositories
├── domain/     # models, interfaces, use cases
├── presentation/
└── di/
```

## Build & run

1. Open project in Android Studio (Gradle root name: **Flipzon**)
2. Sync Gradle
3. Run on emulator/device
4. Login → browse → cart → checkout

`compileSdk 36` · `minSdk 24`

## Submission

- [ ] Public GitHub repository link

