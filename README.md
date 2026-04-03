**🛒 Online Shopping App**

A modern Android e-commerce application built using Kotlin, MVVM, and Clean Architecture. This project demonstrates scalable app design with proper separation of concerns, dependency injection, and reactive data handling.

**🚀 Features**
 🏠 Home screen with featured products & categories
 🔍 Product listing, search, and details
 🛒 Cart management (add, remove, update quantity)
 💳 Checkout flow with address validation
 📦 Order placement and confirmation
 👤 Basic login screen (UI flow)
 🔄 Pagination / load more products
 🧠 Clean architecture with UseCases

** 🧱 Architecture**

This project follows Clean Architecture + MVVM:

UI (Compose Screens)
    ↓
ViewModel
    ↓
UseCases (Domain Layer)
    ↓
Repository (Interface)
    ↓
Data Layer (Local + Remote)

**📂 Project Structure**
com.example.onlineshopping

├── data
│   ├── local        # Room DB (CartDao, CartDb, CartEntity)
│   ├── remote       # APIs (ProductApi, FakeGroceryApi)
│   ├── repository   # Repository implementations
│   ├── mapper       # Data ↔ Domain mapping
│   └── model        # DTOs / API models

├── domain
│   ├── model        # Business models
│   ├── repository   # Repository contracts
│   └── usecase      # Business logic (AddToCart, PlaceOrder, etc.)

├── ui
│   ├── screen       # Compose UI screens
│   ├── viewModel    # ViewModels
│   ├── theme        # UI theming
│   └── util         # UI utilities

├── navigation       # Navigation graph & routes
├── di               # Hilt dependency modules
└── MainActivity.kt

**🛠️ Tech Stack**
. Kotlin
. Jetpack Compose
. MVVM Architecture
. Clean Architecture (UseCases)
. Hilt (Dependency Injection)
. Coroutines & Flow
. Room Database
. Retrofit (API layer)
. Navigation Component

**🔄 Data Flow Example**

Placing an Order:

UI → CheckoutViewModel → PlaceOrderUseCase
   → ProductRepository → API
   → CartRepository → Clear Cart

**📦 Key UseCases**
AddToCartUseCase
GetCartUseCase
PlaceOrderUseCase
GetProductsUseCase
ValidateAddressUseCase

**🧪 Highlights**
✅ Modular & scalable architecture
✅ Proper separation of concerns
✅ No direct ViewModel injection (Hilt best practices)
✅ Repository pattern with multiple data sources
✅ Business logic handled via UseCases

**▶️ Getting Started**
1. Clone the repo: git clone https://github.com/sakshi9/online-shopping-app.git
2. Open in Android Studio
3. Run the app 🚀
   
**📌 Future Improvements**
🔐 Authentication (Firebase/Auth API)
💰 Payment integration
🌐 Real backend integration
❤️ Wishlist feature
📊 Order history

**👩‍💻 Author**
**Sakshi Gupta**

**⭐ If you like this project**
Give it a ⭐ on GitHub!
