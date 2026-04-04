# 🛒 Online Shopping — Android App (Kotlin)

A production-ready grocery shopping Android app built with **Kotlin + Jetpack Compose**, following **Clean Architecture** with distinct data, domain, and UI layers.

---

## 📱 Screens

| Screen | Description |
|---|---|
| **HomeScreen** | Hero banner, category chips, deals & top-rated carousels |
| **ShopScreen** | Product grid with search, sort, filter & infinite scroll |
| **ProductDetailScreen** | Full product info, rating, add to cart, related products |
| **CartScreen** | Item management, quantity controls, delivery threshold |
| **CheckoutScreen** | Address form, delivery slot selection, payment method |
| **OrderConfirmationScreen** | Animated success, full order summary, estimated delivery |
| **LoginScreen** | Email/password with validation, guest login |
| **AccountScreen** | Profile, order history, Clubcard points |

---

## 🏗️ Architecture

Clean Architecture with 3 layers:

```
UI Layer  →  Domain Layer  →  Data Layer
```

- **UI** knows only about Domain (via UseCases)
- **Domain** is pure Kotlin — no Android dependencies
- **Data** implements Domain interfaces

---

## 📁 Project Structure

```
com.example.onlineshopping/
│
├── data/
│   ├── local/
│   │   ├── CartDao             # Room DAO for cart operations
│   │   ├── AppDatabase              # Room database
│   │   └── CartEntity          # Room entity
│   ├── mapper/                 # Data ↔ Domain model mappers
│   ├── model/
│   │   ├── Category            # API response model
│   │   ├── Order               # Order data model
│   │   ├── Product             # Product data model
│   │   └── User                # User profile model
│   ├── remote/
│   │   ├── CartApi             # Cart API interface
│   │   ├── CartDto             # Cart data transfer object
│   │   ├── FakeGroceryApi      # Fake API (simulates network, 33 products)
│   │   └── ProductApi          # Product API interface
│   └── repository/
│       ├── CartRepositoryImpl      # Cart repository implementation
│       └── ProductRepositoryImpl   # Product repository implementation
│
├── di/
│   ├── DatabaseModule          # Hilt: Room DB + DAO providers
│   ├── NetworkModule           # Hilt: Retrofit / API providers
│   └── RepositoryModule        # Hilt: Repository binding providers
│
├── domain/
│   ├── model/
│   │   ├── CartData            # Domain model for cart state
│   │   ├── HomeData            # Domain model for home screen
│   │   └── ProductDetail       # Domain model for product detail
│   ├── repository/
│   │   ├── CartRepository      # Cart repository interface
│   │   └── ProductRepository   # Product repository interface
│   └── usecase/
│       ├── AddToCartUseCase
│       ├── ClearCartUseCase
│       ├── DecrementCartItemUseCase
│       ├── GetCartItemQuantityUseCase
│       ├── GetCartUseCase
│       ├── GetCategoriesUseCase
│       ├── GetCheckoutSummaryUseCase
│       ├── GetDealsUseCase
│       ├── GetFeaturedProductsUseCase
│       ├── GetHomeDataUseCase
│       ├── GetProductDetailUseCase
│       ├── GetProductsUseCase
│       ├── GetSearchSuggestionsUseCase
│       ├── IncrementCartItemUseCase
│       ├── PlaceOrderUseCase
│       ├── RemoveCartItemUseCase
│       └── ValidateAddressUseCase
│
├── navigation/
│   ├── BottomNavItem           # Bottom nav configuration
│   ├── NavGraph                # Navigation graph composable
│   └── Navigation              # Route definitions (sealed class)
│
├── ui/
│   ├── model/                  # UI-specific state models
│   ├── screen/
│   │   ├── AccountScreen
│   │   ├── CartScreen
│   │   ├── CheckoutScreen
│   │   ├── HomeScreen
│   │   ├── LoginScreen
│   │   ├── OrderConfirmationScreen
│   │   ├── ProductDetailScreen
│   │   └── ShopScreen
│   ├── theme/                  # Material3 colors, typography, shapes
│   ├── util/                   # UI utilities / extensions
│   └── viewModel/
│       ├── CartViewModel
│       ├── CheckoutViewModel
│       ├── HomeViewModel
│       ├── LoginViewModel
│       ├── OrderStore          # @Singleton in-memory order state holder
│       ├── OrderViewModel
│       ├── ProductDetailViewModel
│       └── ShopViewModel
│
├── MainActivity                # Single activity, hosts NavHost
└── OnlineShoppingApplication   # @HiltAndroidApp entry point
```

---

## 🔧 Tech Stack

| Component | Library |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | Clean Architecture + MVVM |
| DI | Hilt |
| Navigation | Compose Navigation |
| Local DB (cart) | Room |
| Async | Kotlin Coroutines + StateFlow |
| Images | Coil |
| Fake API | Pure Kotlin with simulated delays |
| Build System | Gradle Version Catalogs |

---

## 🧩 Key Design Patterns

### Clean Architecture
Each layer has a single responsibility:
- **Data layer** handles API calls, database, and mapping
- **Domain layer** contains pure business logic via UseCases — no Android imports
- **UI layer** only calls UseCases, never touches repositories directly

### Use Case per Action
Every user action maps to exactly one UseCase (e.g. `AddToCartUseCase`, `PlaceOrderUseCase`). This keeps ViewModels thin and logic testable in isolation.

### OrderStore (Cross-ViewModel Communication)
`OrderStore` is a `@Singleton` plain class (not a ViewModel) used to pass the confirmed order from `CheckoutViewModel` (write) to `OrderViewModel` (read) after navigation, without violating Hilt's rule against injecting `@HiltViewModel` classes into other classes.

```
CheckoutViewModel  →  orderStore.set(order)
OrderViewModel     →  orderStore.order (StateFlow)
OrderConfirmScreen →  reads via OrderViewModel, calls clearOrder() on exit
```

### Repository Pattern
`CartRepository` and `ProductRepository` are interfaces defined in the domain layer. `CartRepositoryImpl` and `ProductRepositoryImpl` in the data layer implement them. Hilt binds the implementations via `RepositoryModule`.

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
cd OnlineShopping
```

Open in Android Studio and click **Run**, or:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

### No Backend Required
The app uses `FakeGroceryApi` — a pure Kotlin class that simulates:
- ✅ Realistic network latency (random 400–700ms delay)
- ✅ 33 grocery products across 10 categories
- ✅ Search, sort, pagination
- ✅ Order placement with full response object
- ✅ User profile & Clubcard data

---

## 🔌 Dependency Injection (Hilt)

| Module | Provides |
|---|---|
| `DatabaseModule` | `AppDatabase`, `CartDao` |
| `NetworkModule` | `FakeGroceryApi`, `ProductApi`, `CartApi` |
| `RepositoryModule` | `CartRepository → CartRepositoryImpl`, `ProductRepository → ProductRepositoryImpl` |

---

## 👤 Demo Credentials

| Field | Value |
|---|---|
| Email | any valid email (e.g. `test@example.com`) |
| Password | any 4+ characters |
| Guest | tap "Continue as Guest" |

---

## 📦 Cart Persistence

Cart items are persisted using **Room** via `CartEntity` + `CartDao`. The cart survives app restarts. `CartRepositoryImpl` exposes a `Flow<List<CartEntity>>` observed by `CartViewModel`.

---

## 🔄 Data Flow

```
User Action
    ↓
Composable (Screen)
    ↓
ViewModel
    ↓
UseCase (Domain)
    ↓
Repository Interface (Domain)
    ↓
RepositoryImpl (Data)
    ↓
FakeGroceryApi / Room
```

---