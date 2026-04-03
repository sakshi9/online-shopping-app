# 🛒 Online Shopping App — Android (Kotlin)

A production-quality Android grocery shopping app built with **Kotlin + Jetpack Compose**, following modern Android architecture best practices. Developed as a technical assessment submission.

---

## 📱 App Screenshots & Features

| Screen | Features |
|---|---|
| **Home** | Hero banner, category chips, deals carousel, top-rated products, Clubcard banner |
| **Shop** | Search with suggestions, sort/filter, infinite scroll pagination, 33 real products |
| **Product Detail** | Full product info, rating, add to cart with qty control, related products |
| **Cart** | Item management, qty +/−, delivery threshold tracker, order summary |
| **Checkout** | Address form, delivery slots, card/PayPal/Clubcard payment, order placement |
| **Order Confirm** | Animated success, full order summary, estimated delivery |
| **Login** | Email + password, guest login, form validation |
| **Account** | Profile, order history, Clubcard points & redemption |

---

## 🏗️ Architecture & Tech Stack

```
MVVM + Clean Architecture
├── UI Layer        → Jetpack Compose, Material3, StateFlow
├── ViewModel Layer → Hilt-injected ViewModels, coroutines
├── Repository      → Single source of truth, FakeApi + Room
└── Data Layer      → Room (cart), FakeGroceryApi (products/orders)
```

| Component | Library |
|---|---|
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + StateFlow |
| Dependency Injection | Hilt |
| Local DB (cart) | Room |
| Navigation | Compose Navigation |
| Images | Coil |
| Async | Kotlin Coroutines |
| Fake API | Pure Kotlin with simulated delays |
| Build | Gradle Version Catalogs (libs.versions.toml) |

---

## 📁 Project Structure

```
app/src/main/java/com/example/onlineshopping
├── OnlineShopping.kt                  # @HiltAndroidApp entry
├── MainActivity.kt                    # NavHost + BottomNavigation
├── Screen.kt                          # Sealed route definitions
│
├── api/
│   └── FakeGroceryApi.kt              # 33 products, categories, fake order placement
│
├── data/
│   ├── model/Models.kt                # Product, CartItem, Order, User, Address
│   └── repository/
│       ├── CartDao.kt                 # Room DAO + AppDatabase
│       ├── CartRepository.kt          # Cart CRUD with Flow
│       └── ProductRepository.kt       # Wraps FakeApi with runCatching
│
├── di/
│   └── AppModule.kt                   # Hilt: Room + DAO providers
│
└── ui/
    ├── home/
    │   ├── HomeViewModel.kt
    │   └── HomeScreen.kt
    ├── shop/
    │   ├── ShopViewModel.kt           # Search, filter, sort, pagination
    │   ├── ShopScreen.kt              # Grid, search bar, ProductCard
    │   ├── ProductDetailViewModel.kt
    │   └── ProductDetailScreen.kt
    ├── cart/
    │   ├── CartViewModel.kt
    │   └── CartScreen.kt
    ├── checkout/
    │   ├── CheckoutViewModel.kt
    │   ├── CheckoutScreen.kt
    │   └── OrderConfirmScreen.kt
    ├── login/
    │   ├── LoginViewModel.kt
    │   └── LoginScreen.kt
    └── account/
        └── AccountScreen.kt
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK 34
- Min SDK 26 (Android 8.0+)

### Run the App

```bash
git clone https://github.com/sakshi9/online-shopping-app.git
cd onlineShopping

# Open in Android Studio
# OR build from command line:
./gradlew assembleDebug
./gradlew installDebug
```

### No backend required
The app uses a **fully self-contained fake API** (`FakeGroceryApi.kt`) that simulates:
- ✅ Network latency (400–700ms random delay)
- ✅ 33 real grocery products across 10 categories
- ✅ Search, sort, pagination
- ✅ Order placement with realistic response
- ✅ User profile & Clubcard data

---

## 🧪 Key Design Decisions

### 1. Fake API over Mock Server
Rather than setting up WireMock or a real backend, `FakeGroceryApi` is a `@Singleton` Hilt-injectable class that mimics async network behaviour using `delay()`. This makes the app fully self-contained and easy to demo offline.

### 2. Room for Cart Persistence
Cart data persists across app restarts using Room. `CartRepository` exposes `Flow<List<CartItem>>` which all ViewModels observe reactively.

### 3. Unidirectional Data Flow
All screens follow strict UDF: `UiState → Composable → Event → ViewModel`. No state leaks between layers.

### 4. Compose Navigation with Type-Safe Args
`Screen.kt` defines all routes as sealed classes with `createRoute()` helpers, preventing string typos at call sites.

---

## 📋 Product Catalogue

33 products across 10 categories:
`Fruits` · `Vegetables` · `Dairy` · `Bakery` · `Meat & Fish` · `Drinks` · `Snacks` · `Frozen` · `Household`

Products include: name, price, unit, high-res Unsplash image, rating, review count, badge (Organic, Sale, New, etc.), stock status, and description.

---

## 👤 Demo Credentials

- **Email:** any valid email (e.g. `test@gmail.com`)
- **Password:** any 4+ character password
- **Guest:** tap "Continue as Guest"

---