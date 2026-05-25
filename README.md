# Assignment Climbax — E-Commerce Android App

Kotlin Android assignment: product feed, search, local cart (Room), DummyJSON login, and checkout.

## Tech Stack

| Layer | Technology |
|--------|------------|
| Language | Kotlin |
| Architecture | MVVM + Repository pattern |
| UI | XML (ViewBinding), Material Design 3 |
| Networking | Retrofit + OkHttp + Gson |
| Session | **DataStore Preferences** |
| Local DB | Room (cart) |
| Async | Coroutines, Flow, LiveData |
| Images | Coil |
| DI | Manual (`AppContainer`) |

## API

Base URL: `https://dummyjson.com/`

- `POST /auth/login` — Login
- `GET /products?limit=&skip=` — Pagination
- `GET /products/search?q=` — Search (400ms debounce)
- `POST /carts/add` — Checkout

**Test credentials:** `emilys` / `emilyspass`

## Features

### Authentication
- DummyJSON login API
- Session stored in DataStore: `id`, `email`, `firstName`, `lastName`, `image`
- Cold start: Login if no session, else Home (`MainActivity`) with `CLEAR_TASK` (no back stack to login)
- Logout: clears DataStore session **and** Room cart, returns to Login

### Top app bar (all tabs)
- Circular profile image, full name, email (from DataStore only)
- Logout button

### Home
- Paginated RecyclerView (10 per page)
- Loading / Error / Empty + Retry
- Search below toolbar
- Product card: ID, title, thumbnail, price, add to cart / quantity controls

### Cart
- Room `CartEntity`: `productId`, `title`, `price`, `thumbnail`, `quantity`
- Reactive Flow → LiveData
- Increment / decrement, remove at quantity 0
- Order total, checkout API with logged-in user id
- Success → clear cart + Snackbar; failure → keep cart + error Snackbar

## Project Structure

```
app/src/main/java/com/example/assignmentclimbax/
├── data/       # remote, local, prefs (SessionDataStore), repositories
├── domain/     # models, repository interfaces, use cases
├── presentation/
└── di/         # AppContainer
```

## Build & Run

1. Open in Android Studio
2. Sync Gradle
3. Run on device/emulator with internet
4. Login with `emilys` / `emilyspass`

**Requirements:** `compileSdk 36`, `minSdk 24`

## Submission

- [ ] Public GitHub repository link
- [ ] Optional screen recording / GIF
