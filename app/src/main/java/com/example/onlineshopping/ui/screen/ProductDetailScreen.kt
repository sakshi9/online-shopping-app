package com.example.onlineshopping.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.onlineshopping.ui.util.Conversion.Companion.formatValue
import com.example.onlineshopping.ui.viewModel.ProductDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.addedToCart) {
        if (state.addedToCart) snackbarHostState.showSnackbar("Added to cart!")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.product?.name ?: "", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue, titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White)
            )
        },
        bottomBar = {
            state.product?.let { product ->
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(formatValue(product.price), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                            Text("per ${product.unit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        if (state.cartQuantity > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(0.dp),
                                modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(Color.Blue)
                            ) {
                                IconButton(onClick = viewModel::decrementQty, modifier = Modifier.size(48.dp)) {
                                    Icon(Icons.Default.Remove, null, tint = Color.White)
                                }
                                Text("${state.cartQuantity}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                IconButton(onClick = viewModel::incrementQty, modifier = Modifier.size(48.dp)) {
                                    Icon(Icons.Default.Add, null, tint = Color.White)
                                }
                            }
                        } else {
                            Button(
                                onClick = viewModel::addToCart,
                                enabled = product.inStock,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                                shape = RoundedCornerShape(99.dp),
                                modifier = Modifier.height(48.dp).weight(1f)
                            ) {
                                Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (product.inStock) "Add to Cart" else "Out of Stock", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Blue)
            }
        } else if (state.product == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Product not found")
            }
        } else {
            val product = state.product!!
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {

                // Hero image
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                        AsyncImage(
                            model = product.image,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (product.badge != null) {
                            Surface(
                                color = Color.Red,
                                shape = RoundedCornerShape(bottomEnd = 12.dp),
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(product.badge, color = Color.White, fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }
                    }
                }

                // Info card
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text(product.category.replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp, color = Color.Blue, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(product.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))

                        // Rating row
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row {
                                repeat(5) { i ->
                                    Text(if (i < product.rating.toInt()) "★" else "☆",
                                        fontSize = 16.sp, color = Color(0xFFF6AD55))
                                }
                            }
                            Text("${product.rating}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("(${product.reviews} reviews)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        Spacer(Modifier.height(16.dp))

                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))

                        Text("About this product", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(product.description.ifBlank { "A quality product, sourced responsibly." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 22.sp)

                        Spacer(Modifier.height(20.dp))

                        // Availability
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                if (product.inStock) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                null,
                                tint = if (product.inStock) Color.Green else Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                if (product.inStock) "In Stock — Ready for delivery" else "Currently out of stock",
                                color = if (product.inStock) Color.Green else Color.Red,
                                fontWeight = FontWeight.Medium, fontSize = 14.sp
                            )
                        }
                    }
                }

                // Related products
                if (state.related.isNotEmpty()) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                        Text("You might also like", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 12.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.related) { related ->
                                ProductCard(product = related, compact = true, onClick = { onNavigateToProduct(related.id) })
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}