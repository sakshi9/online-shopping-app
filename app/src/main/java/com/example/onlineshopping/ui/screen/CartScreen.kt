package com.example.onlineshopping.ui.screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineshopping.ui.viewModel.CartViewModel
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import com.example.onlineshopping.domain.model.CartData
import com.example.onlineshopping.ui.util.Conversion.Companion.formatValue

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToShop: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue, titleContentColor = Color.White
                ),
                actions = {
                    if (state.items.isNotEmpty()) {
                        TextButton(onClick = viewModel::clearCart) {
                            Text(
                                "Clear All",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.items.isEmpty()) {
            EmptyCart(onShop = onNavigateToShop, modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.productId }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrement = { viewModel.increment(item) },
                            onDecrement = { viewModel.decrement(item) },
                            onRemove = { viewModel.remove(item) }
                        )
                    }

                    // Delivery notice
                    item {
                        Surface(
                            color = if (state.deliveryFee == 0.0) Color.Green.copy(alpha = 0.06f)
                            else Color.Blue.copy(alpha = 0.07f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocalShipping, null,
                                    tint = if (state.deliveryFee == 0.0) Color.Green else Color.Blue,
                                    modifier = Modifier.size(20.dp)
                                )
                                if (state.deliveryFee == 0.0) {
                                    Text(
                                        "🎉 You've unlocked free delivery!",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color.Green
                                    )
                                } else {
                                    val remaining = 400.0 - state.subtotal
                                    Text(
                                        "Add ₹${formatValue(remaining)} " +
                                                "more for free delivery",
                                        fontSize = 13.sp,
                                        color = Color.Blue,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Order summary
                Surface(shadowElevation = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryRow("Subtotal", "₹${formatValue(state.subtotal)}")
                        SummaryRow(
                            "Delivery",
                            if (state.deliveryFee == 0.0) "FREE" else formatValue(state.deliveryFee),
                            valueColor = if (state.deliveryFee == 0.0) Color.Green else MaterialTheme.colorScheme.onSurface
                        )
                        HorizontalDivider()
                        SummaryRow("Total", formatValue(state.total), isBold = true)
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            shape = RoundedCornerShape(99.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Proceed to Checkout",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun CartItemRow(
    item: CartData,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = item.image,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    item.name, style = MaterialTheme.typography.titleMedium, maxLines = 2,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${formatValue(item.price)} / ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
                Text(
                    formatValue(item.price * item.quantity),
                    fontWeight = FontWeight.Bold, color = Color.Blue, fontSize = 15.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        "Remove",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.Blue)
                ) {
                    IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Remove,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        "${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value, fontWeight = if (isBold) FontWeight.Black else FontWeight.SemiBold,
            fontSize = if (isBold) 18.sp else 14.sp, color = valueColor
        )
    }
}

@Composable
private fun EmptyCart(onShop: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("🛒", fontSize = 64.sp)
            Text(
                "Your cart is empty",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Add some items to get started",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Button(
                onClick = onShop,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                shape = RoundedCornerShape(99.dp)
            ) {
                Text("Start Shopping", fontWeight = FontWeight.Bold)
            }
        }
    }
}