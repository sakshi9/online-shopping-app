package com.example.onlineshopping.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.onlineshopping.data.model.Category
import com.example.onlineshopping.ui.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToShop: (category: String) -> Unit,
    onNavigateToProduct: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero Banner
        item { HeroBanner(onShopNow = { onNavigateToShop("all") }) }

        // Categories
        item {
            SectionHeader(title = "Shop by Category", emoji = "🛒")
            if (state.isLoading) {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(6) { ShimmerCategoryChip() }
                }
            } else {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.categories.filter { it.id != "all" }) { cat ->
                        CategoryChip(cat, onClick = { onNavigateToShop(cat.id) })
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Today's Deals
        item {
            SectionHeader(title = "Today's Deals", emoji = "🔥", actionText = "See all", onAction = { onNavigateToShop("all") })
        }
        if (state.isLoading) {
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(4) { ShimmerProductCard() }
                }
            }
        } else {
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.deals) { product ->
                        ProductCard(product = product, compact = true, onClick = { onNavigateToProduct(product.id) })
                    }
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }

        // Top Rated
        item {
            SectionHeader(title = "Top Rated", emoji = "⭐", actionText = "See all", onAction = { onNavigateToShop("all") })
        }
        if (state.isLoading) {
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(4) { ShimmerProductCard() }
                }
            }
        } else {
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.featured) { product ->
                        ProductCard(product = product, compact = true, onClick = { onNavigateToProduct(product.id) })
                    }
                }
            }
        }

        // Clubcard Banner
        item {
            Spacer(Modifier.height(16.dp))
            ClubcardBanner()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeroBanner(onShopNow: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Brush.linearGradient(listOf(Color.Blue, Color.Cyan)))
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&q=70",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(
            listOf(Color.Black.copy(alpha = 0.85f), Color.Blue.copy(alpha = 0.6f))
        )))
        Column(
            modifier = Modifier.align(Alignment.CenterStart).padding(24.dp)
        ) {
            Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(99.dp)) {
                Text("🛒 Free delivery over ₹400", color = Color.White, fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Fresh groceries,", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("delivered fast", color = Color.Yellow, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onShopNow,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(99.dp)
            ) {
                Text("Shop Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, emoji: String, actionText: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$emoji  $title", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionText, color = Color.Blue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun CategoryChip(category: Category, onClick: () -> Unit) {
    val bgColor = remember(category.color) {
        try { Color(android.graphics.Color.parseColor(category.color)) } catch (e: Exception) { Color.Blue }
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = bgColor.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, bgColor.copy(alpha = 0.3f)),
        modifier = Modifier.width(90.dp).height(80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(category.icon, fontSize = 26.sp)
            Spacer(Modifier.height(4.dp))
            Text(category.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
        }
    }
}

@Composable
private fun ClubcardBanner() {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1A365D), Color.Blue)))
            .padding(20.dp)
    ) {
        Column {
            Text("Shopping Clubcard", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Text("Earn points and Redeem for vouchers.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
        Text("1,250 pts", color = Color.Yellow, fontSize = 22.sp, fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
fun ShimmerProductCard() {
    Box(modifier = Modifier.width(160.dp).height(220.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant))
}

@Composable
fun ShimmerCategoryChip() {
    Box(modifier = Modifier.width(90.dp).height(80.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant))
}