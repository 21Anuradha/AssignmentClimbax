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

## How Room cart works (local storage)

Cart items are **not** stored on the server until checkout. They live in SQLite via Room:

| File | Role |
|------|------|
| `CartEntity.kt` | Table `cart_items` columns |
| `CartDao.kt` | Insert / update / delete / observe `Flow` |
| `AppDatabase.kt` | Room database `assignment_climbax_db` |
| `CartRepositoryImpl.kt` | Home add-to-cart writes here; Cart tab reads `Flow` |

**Home → Add to Cart:** `CartDao.upsert()` or `updateQuantity()`  
**Cart tab:** `observeCartItems()` → UI updates automatically  
**Checkout success:** `clearAll()`  
**Logout:** session + cart cleared  

## Test cart + offline mode

### On the device (quick test)

1. Login with `emilys` / `emilyspass` (internet **ON**).
2. **Home:** tap **Add to Cart** on 1–2 products → Snackbar + quantity `+ / −` on card.
3. Open **Cart** tab → items and total price should appear.
4. Turn **Airplane mode ON** (or disable Wi‑Fi/mobile data).
5. **Cart** tab again → items still visible (proves Room offline).
6. Change quantity with `+ / −` → total updates (still offline).
7. Turn internet **ON** only for **Checkout** (API needs network).
8. **Do not logout** during cart test — logout clears the local cart by design.

### Android Studio Database Inspector

1. Run the app in **debug** on emulator/device.
2. **View → Tool Windows → App Inspection** (or **Database Inspector**).
3. Select process `com.example.assignmentclimbax`.
4. Open database **`assignment_climbax_db`** → table **`cart_items`**.
5. Add a product in the app → refresh inspector → new row with `productId`, `title`, `price`, `thumbnail`, `quantity`.

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
