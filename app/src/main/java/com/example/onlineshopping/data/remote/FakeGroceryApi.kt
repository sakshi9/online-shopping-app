package com.example.onlineshopping.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.data.model.ProductsResponse
import com.example.onlineshopping.data.model.User
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.ui.model.DeliverySlot
import kotlinx.coroutines.delay
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FakeGroceryApi @Inject constructor() {

    private val categories = listOf(
        Category("all", "All", "🛒", "#005EB8"),
        Category("fruits", "Fruits", "🍎", "#E63946"),
        Category("vegetables", "Vegetables", "🥦", "#2A9D8F"),
        Category("dairy", "Dairy", "🧀", "#F4A261"),
        Category("bakery", "Bakery", "🍞", "#C77B3A"),
        Category("meat", "Meat & Fish", "🥩", "#D62828"),
        Category("drinks", "Drinks", "🧃", "#4895EF"),
        Category("snacks", "Snacks", "🍿", "#7B2D8B"),
        Category("frozen", "Frozen", "🧊", "#0096C7"),
        Category("household", "Household", "🧹", "#6C757D"),
    )

    private val products = listOf(
        Product(
            "p1",
            "Gala Apples",
            "fruits",
            250.50,
            "kg",
            "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400&q=80",
            "Organic",
            4.7,
            214,
            true,
            "Sweet, crisp apples grown in the English countryside. Perfect for snacking or baking."
        ),
        Product(
            "p2",
            "Ripe Bananas",
            "fruits",
            50.89,
            "bunch",
            "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400&q=80",
            null,
            4.5,
            189,
            true,
            "Sun-ripened bananas, naturally sweet and full of potassium. Great for smoothies."
        ),
        Product(
            "p3",
            "Seedless Grapes",
            "fruits",
            150.25,
            "500g",
            "https://images.unsplash.com/photo-1537640538966-79f369143f8f?w=400&q=80",
            "Sale",
            4.6,
            97,
            true,
            "Plump, juicy seedless grapes. No pips, all pleasure."
        ),
        Product(
            "p4",
            "Strawberries",
            "fruits",
            200.00,
            "400g",
            "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=400&q=80",
            "New",
            4.8,
            302,
            true,
            "British strawberries at the peak of ripeness. Gloriously sweet."
        ),
        Product(
            "p5",
            "Alphonso Mango",
            "fruits",
            180.20,
            "each",
            "https://images.unsplash.com/photo-1601493700631-2b16ec4b4716?w=400&q=80",
            null,
            4.4,
            76,
            false,
            "The king of mangoes. Rich, creamy flesh with a heavenly aroma."
        ),
        Product(
            "p6",
            "Blueberries",
            "fruits",
            190.50,
            "150g",
            "https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=400&q=80",
            "Organic",
            4.9,
            421,
            true,
            "Plump, antioxidant-rich blueberries. Organic certified."
        ),
        Product(
            "p7",
            "Tenderstem Broccoli",
            "vegetables",
            32.35,
            "200g",
            "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=400&q=80",
            null,
            4.3,
            88,
            true,
            "Tender, sweet stems. Ready to stir-fry or steam in minutes."
        ),
        Product(
            "p8",
            "Baby Spinach",
            "vegetables",
            25.10,
            "200g",
            "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400&q=80",
            "Organic",
            4.6,
            155,
            true,
            "Organic baby spinach leaves. Washed and ready to eat."
        ),
        Product(
            "p9",
            "Cherry Tomatoes",
            "vegetables",
            80.60,
            "300g",
            "https://images.unsplash.com/photo-1561136594-7f68413baa99?w=400&q=80",
            null,
            4.5,
            200,
            true,
            "Bursting with sweetness. Perfect for salads or roasting."
        ),
        Product(
            "p10",
            "Carrots",
            "vegetables",
            48.75,
            "kg",
            "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400&q=80",
            "Sale",
            4.2,
            60,
            true,
            "Crunchy British carrots. Great raw, roasted, or in soups."
        ),
        Product(
            "p11",
            "Red Bell Peppers",
            "vegetables",
            57.85,
            "each",
            "https://images.unsplash.com/photo-1525607551316-4a8e16d1f9ba?w=400&q=80",
            null,
            4.4,
            73,
            true,
            "Vibrant red peppers, naturally sweet. Packed with vitamin C."
        ),
        Product(
            "p12",
            "Whole Milk",
            "dairy",
            72.09,
            "2L",
            "https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400&q=80",
            null,
            4.5,
            510,
            true,
            "Full-fat British milk. Rich, creamy and fresh."
        ),
        Product(
            "p13",
            "Greek Yogurt",
            "dairy",
            49.80,
            "500g",
            "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400&q=80",
            "Low Fat",
            4.7,
            340,
            true,
            "Thick, creamy Greek-style yogurt. High in protein, low in fat."
        ),
        Product(
            "p14",
            "Cheddar Cheese",
            "dairy",
            230.95,
            "400g",
            "https://images.unsplash.com/photo-1452195100486-9cc805987862?w=400&q=80",
            null,
            4.6,
            278,
            true,
            "Mature British Cheddar. Sharp, nutty and full-flavoured."
        ),
        Product(
            "p15",
            "Free Range Eggs",
            "dairy",
            60.40,
            "6 pack",
            "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=400&q=80",
            "Free Range",
            4.8,
            620,
            true,
            "6 large free-range eggs from British hens with outdoor access."
        ),
        Product(
            "p16",
            "Salted Butter",
            "dairy",
            200.65,
            "250g",
            "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=400&q=80",
            null,
            4.4,
            190,
            true,
            "Rich, creamy salted butter. Made from fresh British cream."
        ),
        Product(
            "p17",
            "Sourdough Loaf",
            "bakery",
            400.75,
            "800g",
            "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=400&q=80",
            "Artisan",
            4.9,
            386,
            true,
            "Stone-baked sourdough with a crisp crust and chewy crumb. Slowly fermented."
        ),
        Product(
            "p18",
            "Croissants",
            "bakery",
            90.50,
            "4 pack",
            "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400&q=80",
            "Fresh",
            4.8,
            290,
            true,
            "Buttery, flaky croissants. Baked fresh in store every morning."
        ),
        Product(
            "p19",
            "Bagels",
            "bakery",
            54.80,
            "5 pack",
            "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=400&q=80",
            null,
            4.3,
            112,
            true,
            "Authentic New York-style bagels. Boiled then baked for the perfect chew."
        ),
        Product(
            "p20",
            "Chicken Breast",
            "meat",
            450.50,
            "500g",
            "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400&q=80",
            null,
            4.5,
            415,
            true,
            "Lean, skinless British chicken breast. High in protein, low in fat."
        ),
        Product(
            "p21",
            "Salmon Fillet",
            "meat",
            500.99,
            "300g",
            "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400&q=80",
            "Wild Caught",
            4.8,
            210,
            true,
            "Atlantic salmon fillet. Rich in omega-3s and natural flavour."
        ),
        Product(
            "p23",
            "Orange Juice",
            "drinks",
            30.00,
            "1L",
            "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=400&q=80",
            "No Added Sugar",
            4.6,
            320,
            true,
            "Freshly squeezed orange juice. Not from concentrate. No added sugar."
        ),
        Product(
            "p24",
            "Sparkling Water",
            "drinks",
            15.65,
            "1.5L",
            "https://images.unsplash.com/photo-1616118132534-381148898bb4?w=400&q=80",
            null,
            4.3,
            88,
            true,
            "Naturally carbonated spring water. Crisp and refreshing."
        ),
        Product(
            "p25",
            "Oat Milk",
            "drinks",
            70.75,
            "1L",
            "https://images.unsplash.com/photo-1600718374662-0483d2b9da44?w=400&q=80",
            "Vegan",
            4.7,
            234,
            true,
            "Barista-grade oat milk. Perfectly froths for lattes and cappuccinos."
        ),
        Product(
            "p26",
            "Mixed Nuts",
            "snacks",
            600.20,
            "200g",
            "https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=400&q=80",
            null,
            4.5,
            167,
            true,
            "A blend of cashews, almonds, hazelnuts and walnuts. Unsalted and natural."
        ),
        Product(
            "p27",
            "Dark Chocolate Bar",
            "snacks",
            100.50,
            "100g",
            "https://images.unsplash.com/photo-1606312619070-d48b4c652a52?w=400&q=80",
            "70% Cocoa",
            4.8,
            452,
            true,
            "Rich, smooth 70% dark chocolate. Ethically sourced cocoa beans."
        ),
        Product(
            "p28",
            "Kettle Chips",
            "snacks",
            10.89,
            "150g",
            "https://images.unsplash.com/photo-1621447504864-d8686e12698c?w=400&q=80",
            null,
            4.6,
            312,
            true,
            "Thick-cut, hand-cooked crisps. Sea salt and balsamic vinegar flavour."
        ),
        Product(
            "p29",
            "Garden Peas",
            "frozen",
            89.00,
            "750g",
            "https://images.unsplash.com/photo-1587735243615-c03f25aaff15?w=400&q=80",
            null,
            4.3,
            95,
            true,
            "Frozen garden peas. Picked and frozen within hours to lock in freshness."
        ),
        Product(
            "p30",
            "Margherita Pizza",
            "frozen",
            750.50,
            "360g",
            "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400&q=80",
            null,
            4.1,
            203,
            true,
            "Classic stone-baked margherita. Crispy base, tangy tomato, melted mozzarella."
        ),
        Product(
            "p31",
            "Washing Liquid",
            "household",
            600.00,
            "1.5L",
            "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=400&q=80",
            null,
            4.4,
            130,
            true,
            "Concentrated bio washing liquid. Removes tough stains at 30°C."
        ),
        Product(
            "p32",
            "Kitchen Roll",
            "household",
            155.20,
            "3 pack",
            "https://images.unsplash.com/photo-1584820927498-cfe5211fd8bf?w=400&q=80",
            null,
            4.2,
            68,
            true,
            "Super absorbent kitchen roll. Pack of 3 double-length rolls."
        ),
    )

    private suspend fun simulateNetwork(ms: Long = 500) {
        delay(ms + Random.Default.nextLong(0, 300))
    }

    // ── Public API ───────────────────────────────────────────

    suspend fun getCategories(): List<Category> {
        simulateNetwork(200)
        return categories
    }

    suspend fun getProducts(
        category: String = "all",
        search: String = "",
        sort: String = "default",
        page: Int = 1,
        pageSize: Int = 12
    ): ProductsResponse {
        simulateNetwork()
        var result = products.toList()

        if (category != "all") result = result.filter { it.category == category }
        if (search.isNotBlank()) {
            val q = search.lowercase()
            result = result.filter { it.name.lowercase().contains(q) || it.category.lowercase().contains(q) }
        }
        result = when (sort) {
            "price_asc"  -> result.sortedBy { it.price }
            "price_desc" -> result.sortedByDescending { it.price }
            "rating"     -> result.sortedByDescending { it.rating }
            "name"       -> result.sortedBy { it.name }
            else         -> result
        }

        val total = result.size
        val start = (page - 1) * pageSize
        val items = result.drop(start).take(pageSize)
        return ProductsResponse(items, total, page, (total + pageSize - 1) / pageSize)
    }

    suspend fun getProductById(id: String): Product {
        simulateNetwork(300)
        return products.find { it.id == id } ?: throw Exception("Product not found")
    }

    suspend fun getRelatedProducts(productId: String): List<Product> {
        simulateNetwork(300)
        val product = products.find { it.id == productId } ?: return emptyList()
        return products.filter { it.category == product.category && it.id != productId }.take(4)
    }

    suspend fun getFeaturedProducts(): List<Product> {
        simulateNetwork(300)
        return products.filter { it.inStock }.sortedByDescending { it.rating }.take(8)
    }

    suspend fun getDeals(): List<Product> {
        simulateNetwork(250)
        return products.filter { it.badge == "Sale" || it.badge == "Organic" }.take(6)
    }

    suspend fun searchSuggestions(query: String): List<Product> {
        simulateNetwork(150)
        if (query.isBlank()) return emptyList()
        return products.filter { it.name.lowercase().contains(query.lowercase()) }.take(5)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun placeOrder(
        items: List<CartData>,
        address: Address,
        paymentMethod: String,
        selectedSlot : DeliverySlot
    ): Order {
        simulateNetwork(1200)
        val subtotal = items.sumOf { it.price * it.quantity }
        val deliveryFee = if (subtotal >= 400.0) 0.0 else 20.0
        return Order(
            id = "OS-" + System.currentTimeMillis().toString(36).uppercase(),
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            total = subtotal + deliveryFee,
            address = address,
            paymentMethod = paymentMethod,
            status = "confirmed",
            estimatedDelivery = selectedSlot.time,
            placedAt = Instant.now().toString()
        )
    }

    fun getFakeUser() = User(
        id = "u1",
        name = "Sakshi Gupta",
        email = "sakshi.libra9@gmail.com",
        clubcard = "6342 8819 2231 0042",
        points = 1250,
        address = Address("Whitefield", "Bengaluru", "560045")
    )
}