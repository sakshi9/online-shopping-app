# 🛒 Online Shopping App — Android (Kotlin)

A production-ready e-commerce grocery shopping Android app built with 
**Kotlin + Jetpack Compose**, following **Clean Architecture** with Use Cases, Repository pattern, Paging3 with Room as offline cache, and full ViewModel unit test coverage.

---

## 📱 Screens

| Screen | Description |
|---|---|
| **Home** | Hero banner, category chips, deals carousel, top-rated products |
| **Shop** | Paged product grid with search, sort, filter — backed by Room cache |
| **Product Detail** | Full product info, rating, add to cart, related products |
| **Cart** | Item management, quantity controls, free delivery threshold tracker |
| **Checkout** | Address form, delivery slot selection, payment method |
| **Order Confirmation** | Animated success screen with full order summary |
| **Login** | Email/password validation, guest login |
| **Account** | Profile, order history, loyalty points & redemption |

---

## 🏗️ Architecture

Clean Architecture with 3 strict layers:

```
UI Layer  →  Domain Layer  →  Data Layer
```

- **UI** only calls UseCases — never touches repositories directly
- **Domain** is pure Kotlin — zero Android framework dependencies
- **Data** implements domain interfaces and owns all I/O (network, Room)

---

## 📁 Project Structure

```
com.example.onlineshopping/
│
├── data/
│   ├── local/
│   │   ├── AppDatabase             # Room database
│   │   ├── CartDao                 # Room DAO — cart CRUD + Flow
│   │   ├── CartEntity              # Room entity for cart items
│   │   ├── ProductDao              # Room DAO — PagingSource for product cache
│   │   ├── ProductEntity           # Room entity for cached products
│   │   ├── RemoteKeysDao           # Room DAO — next/prev page bookmarks
│   │   └── RemoteKeysEntity        # Room entity for Paging3 remote keys
│   ├── mapper/                     # Data ↔ Domain model mappers
│   ├── model/
│   │   ├── Category
│   │   ├── Order
│   │   ├── Product
│   │   ├── ProductsResponse
│   │   └── User
│   ├── remote/
│   │   ├── CartApi
│   │   ├── CartDto
│   │   ├── FakeGroceryApi          # Simulated API with realistic delays
│   │   └── ProductApi
│   └── repository/
│       ├── CartRepositoryImpl
│       ├── ProductRepositoryImpl   # Returns Flow<PagingData<Product>>
│       └── ProductRemoteMediator   # Paging3 RemoteMediator (network → Room)
│
├── di/
│   ├── DatabaseModule              # Room DB, CartDao, ProductDao, RemoteKeysDao
│   ├── NetworkModule               # API providers
│   └── RepositoryModule            # Interface → Impl bindings
│
├── domain/
│   ├── model/
│   │   ├── CartData
│   │   ├── CartSummary
│   │   ├── HomeData
│   │   └── ProductDetail
│   ├── repository/
│   │   ├── CartRepository          # Interface
│   │   └── ProductRepository       # Interface
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
│   ├── BottomNavItem
│   ├── NavGraph
│   └── Navigation
│
├── ui/
│   ├── model/                      # UI-specific state models
│   ├── screen/
│   │   ├── AccountScreen
│   │   ├── CartScreen
│   │   ├── CheckoutScreen
│   │   ├── HomeScreen
│   │   ├── LoginScreen
│   │   ├── OrderConfirmationScreen
│   │   ├── ProductDetailScreen
│   │   └── ShopScreen              # Uses collectAsLazyPagingItems()
│   ├── theme/
│   ├── util/
│   └── viewModel/
│       ├── CartViewModel
│       ├── CheckoutViewModel
│       ├── HomeViewModel
│       ├── LoginViewModel
│       ├── OrderStore              # @Singleton in-memory order bridge
│       ├── OrderViewModel
│       ├── ProductDetailViewModel
│       └── ShopViewModel           # Drives PagingData via flatMapLatest
│
├── test/
│   └── com.example.onlineshopping/
│       ├── ShopViewModelTest
│       ├── CartViewModelTest
│       ├── HomeViewModelTest
│       ├── CheckoutViewModelTest
│       ├── ProductDetailViewModelTest
│
├── MainActivity
└── OnlineShoppingApplication
```

---

## 🔧 Tech Stack

| Component | Library |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | Clean Architecture + MVVM |
| Dependency Injection | Hilt |
| Navigation | Compose Navigation |
| Pagination | **Paging 3** |
| Local Cache | **Room** (product cache + cart persistence) |
| Async | Kotlin Coroutines + StateFlow + Flow |
| Images | Coil |
| Unit Testing | JUnit4 + Mockito + Turbine + Coroutines Test |
| Build System | Gradle Version Catalogs |

---

## 📦 Paging 3 + Room Integration

### How it works

```
ShopViewModel
    │
    │  flatMapLatest(category, search, sort)
    ▼
GetProductsUseCase
    │
    │  Flow<PagingData<Product>>
    ▼
ProductRepository.getProductsPaged()
    │
    │  Pager(remoteMediator, pagingSourceFactory)
    ▼
┌─────────────────────────────────────────────┐
│           ProductRemoteMediator             │
│                                             │
│  REFRESH → fetch page 1 from API            │
│          → clear stale Room cache           │
│          → save products + RemoteKeys       │
│                                             │
│  APPEND  → read nextPage from RemoteKeys   │
│          → fetch next page from API         │
│          → append to Room cache             │
│                                             │
│  PREPEND → endOfPaginationReached = true   │
└──────────────────┬──────────────────────────┘
                   │ writes to
                   ▼
              Room Database
         ┌────────────────────┐
         │  products table    │  ← ProductDao.getProductsPaged()
         │  remote_keys table │  ← RemoteKeysDao
         └────────┬───────────┘
                  │ PagingSource<Int, ProductEntity>
                  ▼
            ShopScreen
     collectAsLazyPagingItems()
```

### Key design decisions

**Room as single source of truth** — the UI never reads directly from the network. It always reads from Room. The `RemoteMediator` fills Room when the cache runs low.

**RemoteKeysEntity** — one row per filter combination (`category-search-sort`). Stores `nextPage` and `prevPage` so the mediator knows where to continue after the cached pages run out.

**ProductEntity cache key** — `"$productId-$category-$search-$sort"` — the same product can exist in multiple filter result sets without overwriting other sets.

**10-minute cache freshness** — `initialize()` checks `RemoteKeysEntity.lastUpdated`. If the cache is less than 10 minutes old, it returns `SKIP_INITIAL_REFRESH` and shows cached data instantly without hitting the network.

**`flatMapLatest` in ShopViewModel** — when the user changes category, sort, or search, `flatMapLatest` cancels the previous `PagingData` flow and starts a new one. No manual page reset needed.

```kotlin
val products: Flow<PagingData<Product>> = _uiState
    .map { Triple(it.selectedCategory, it.searchQuery, it.sortBy) }
    .distinctUntilChanged()
    .flatMapLatest { (category, search, sort) ->
        getProductsUseCase(category, search, sort)
    }
    .cachedIn(viewModelScope)
```

---

## 🧪 ViewModel Unit Tests

Each ViewModel is tested in isolation using fakes — no real Room database or network calls.

### Tools

| Library | Purpose |
|---|---|
| `JUnit4` | Test runner |
| `kotlinx-coroutines-test` | `runTest`, `TestDispatcher`, `advanceUntilIdle` |
| `Turbine` | Flow assertion — `flow.test { awaitItem() }` |
| `Mockito` / `MockK` | Mock UseCases and Repositories |
| `androidx.paging:paging-testing` | `TestPager`, fake `PagingData` for Paging3 |

### What is tested

**ShopViewModelTest**
- Initial state has correct defaults (`category = "all"`, `sort = "default"`)
- `setCategory()` resets `searchQuery` and emits new `PagingData`
- `setSort()` triggers new paging stream via `flatMapLatest`
- `onSearchQueryChange()` debounces and calls `GetSearchSuggestionsUseCase`
- `submitSearch()` clears suggestions
- Filter change cancels previous paging flow (`distinctUntilChanged`)

**CartViewModelTest**
- Cart items collected from `CartRepository` flow correctly
- Subtotal calculated correctly for multiple items
- Delivery fee is `£0` when subtotal ≥ `£40`, `£3.99` otherwise
- `increment()` / `decrement()` / `remove()` call correct repository methods
- `clearCart()` empties the cart

**HomeViewModelTest**
- `HomeUiState` starts with `isLoading = true`
- After load, `featured`, `deals`, `categories` are populated
- Error from repository sets `error` in state

**CheckoutViewModelTest**
- Address validation rejects blank fields
- `placeOrder()` calls `PlaceOrderUseCase` with correct parameters
- On success, `OrderStore.set()` is called and `order` is set in state
- On failure, `error` message is set in state
- Delivery slot change updates `selectedSlot` in state

**ProductDetailViewModelTest**
- Loads product by ID from `SavedStateHandle`
- Related products loaded in same call
- `addToCart()` calls `AddToCartUseCase`
- `increment()` / `decrement()` update cart quantity

**LoginViewModelTest**
- Empty email/password shows validation error
- Invalid email format shows error
- Password shorter than 4 characters shows error
- Valid credentials set `isLoggedIn = true`
- `guestLogin()` sets `isLoggedIn = true` without credentials

### Example test

```kotlin
@Test
fun `setCategory resets search query and triggers new paging stream`() = runTest {
    val viewModel = ShopViewModel(fakeGetProductsUseCase, fakeSearchUseCase)

    viewModel.setCategory("fruits")

    viewModel.uiState.test {
        val state = awaitItem()
        assertEquals("fruits", state.selectedCategory)
        assertEquals("", state.searchQuery)
    }
}

@Test
fun `cart subtotal is correct and delivery fee is free over 40`() = runTest {
    val items = listOf(
        CartItem("p1", "Apples", 25.00, "", "kg", 1),
        CartItem("p2", "Milk",   20.00, "", "2L", 1)
    )
    fakeCartRepository.emit(items)

    cartViewModel.uiState.test {
        val state = awaitItem()
        assertEquals(45.00, state.subtotal, 0.001)
        assertEquals(0.0,   state.deliveryFee, 0.001)
        assertEquals(45.00, state.total, 0.001)
    }
}
```

---

## 🔌 Dependency Injection (Hilt)

| Module | Provides |
|---|---|
| `DatabaseModule` | `AppDatabase`, `CartDao`, `ProductDao`, `RemoteKeysDao` |
| `NetworkModule` | `FakeGroceryApi`, `ProductApi`, `CartApi` |
| `RepositoryModule` | `CartRepository → CartRepositoryImpl`, `ProductRepository → ProductRepositoryImpl` |

`OrderStore` uses `@Singleton` + `@Inject constructor()` — no module entry needed.

---

## 🔄 Full Data Flow

```
User Action (tap category / search / scroll)
        ↓
Composable (ShopScreen)
        ↓  collectAsLazyPagingItems()
ShopViewModel.products: Flow<PagingData<Product>>
        ↓  flatMapLatest
GetProductsUseCase
        ↓
ProductRepository.getProductsPaged()
        ↓  Pager
ProductRemoteMediator  ←→  FakeGroceryApi (network)
        ↓  withTransaction
Room Database (products + remote_keys tables)
        ↓  PagingSource
LazyVerticalGrid renders items
LoadState.Append.Loading → spinner at bottom
LoadState.Refresh.Error  → error + retry button
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK 34
- Min SDK 26 (Android 8.0+)

### Run the app

```bash
git clone https://github.com/YOUR_USERNAME/OnlineShopping.git
cd OnlineShopping
./gradlew assembleDebug
./gradlew installDebug
```

### Run unit tests

```bash
./gradlew test
```

### No backend required

`FakeGroceryApi` is a pure Kotlin class simulating realistic network behaviour:
- ✅ Random latency (400–700ms per call)
- ✅ 33 products across 10 categories
- ✅ Filtering, sorting, pagination
- ✅ Order placement with full response
- ✅ User profile and loyalty points data

---

## 👤 Demo Credentials

| Field | Value |
|---|---|
| Email | any valid email e.g. `user@example.com` |
| Password | any 4+ characters |
| Guest | tap **Continue as Guest** |

---

## 📄 License

This project is submitted as a technical assessment. All rights reserved.