package com.example.onlineshopping.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.onlineshopping.data.model.Product
import com.example.onlineshopping.ui.viewModel.ShopViewModel

val SORT_OPTIONS = listOf(
    "default"    to "Featured",
    "price_asc"  to "Price: Low to High",
    "price_desc" to "Price: High to Low",
    "rating"     to "Top Rated",
    "name"       to "Name A–Z"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    initialCategory: String = "all",
    initialSearch: String   = "",
    onProductClick: (String) -> Unit,
    viewModel: ShopViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val pagingItems = viewModel.products.collectAsLazyPagingItems()

    var showSortSheet by remember { mutableStateOf(false) }

    // Apply initial filters from nav args
    LaunchedEffect(initialCategory, initialSearch) {
        if (initialCategory != "all") viewModel.setCategory(initialCategory)
        if (initialSearch.isNotBlank()) {
            viewModel.onSearchQueryChange(initialSearch)
            viewModel.submitSearch()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        SearchBar(
            query       = uiState.searchQuery,
            suggestions = suggestions,
            onQueryChange = viewModel::onSearchQueryChange,
            onSearch = {
                viewModel.submitSearch()
                pagingItems.refresh()   // reset paging to page 1
            },
            onSuggestionClick = { product ->
                viewModel.onSearchQueryChange(product.name)
                viewModel.submitSearch()
                viewModel.clearSuggestions()
                pagingItems.refresh()
            }
        )

        // ── Filter row ──────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show item count from paging metadata when available
            val itemCount = pagingItems.itemCount
            Text(
                if (itemCount > 0) "$itemCount products" else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            FilterChip(
                selected = uiState.sortBy != "default",
                onClick = { showSortSheet = true },
                label = {
                    Text(
                        SORT_OPTIONS.find { it.first == uiState.sortBy }?.second ?: "Sort",
                        fontSize = 12.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Sort, null, modifier = Modifier.size(16.dp)) }
            )
        }

        // ── Product grid ────────────────────────────────────
        when {
            // Initial load — show shimmer skeletons
            pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(8) { ShimmerProductGridCard() }
                }
            }

            // Initial load error
            pagingItems.loadState.refresh is LoadState.Error -> {
                val error = (pagingItems.loadState.refresh as LoadState.Error).error
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😕  Something went wrong", style = MaterialTheme.typography.titleMedium)
                        Text(
                            error.localizedMessage ?: "Unknown error",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { pagingItems.retry() }) { Text("Retry") }
                    }
                }
            }

            // Empty result after successful load
            pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0 -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No products found", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }

            // Normal grid
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // itemKey tells Compose how to uniquely identify each item
                    items(
                        count = pagingItems.itemCount,
                        key   = pagingItems.itemKey { it.id }
                    ) { index ->
                        val product = pagingItems[index]
                        if (product != null) {
                            ProductCard(
                                product  = product,
                                onClick  = { onProductClick(product.id) }
                            )
                        } else {
                            // Placeholder while item is loading
                            ShimmerProductGridCard()
                        }
                    }

                    // Append loading — more pages being fetched
                    when (val appendState = pagingItems.loadState.append) {
                        is LoadState.Loading -> {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color    = Color.Blue
                                    )
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item(span = { GridItemSpan(2) }) {
                                Row(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Failed to load more",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 13.sp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    TextButton(onClick = { pagingItems.retry() }) {
                                        Text("Retry", color = Color.Blue)
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    // ── Sort bottom sheet ───────────────────────────────────
    if (showSortSheet) {
        ModalBottomSheet(onDismissRequest = { showSortSheet = false }) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(
                    "Sort by",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
                SORT_OPTIONS.forEach { (value, label) ->
                    ListItem(
                        headlineContent = { Text(label) },
                        trailingContent = {
                            if (uiState.sortBy == value) Icon(Icons.Default.Check, null, tint = Color.Blue)
                        },
                        modifier = Modifier.clickable {
                            viewModel.setSort(value)
                            pagingItems.refresh()   // reset paging with new sort
                            showSortSheet = false
                        }
                    )
                }
            }
        }
    }
}

// ── SearchBar ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    suggestions: List<Product>,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSuggestionClick: (Product) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.Blue)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search products…", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { onQueryChange(""); onSearch() }) {
                        Icon(Icons.Default.Clear, null)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(99.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor      = Color.Transparent,
                unfocusedBorderColor    = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            )
        )
        if (suggestions.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Column {
                    suggestions.forEach { product ->
                        ListItem(
                            headlineContent    = { Text(product.name, fontSize = 14.sp) },
                            supportingContent  = { Text(product.category, fontSize = 12.sp, color = Color.Blue) },
                            leadingContent     = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                            modifier           = Modifier.clickable { onSuggestionClick(product) }
                        )
                    }
                }
            }
        }
    }
}

// ── ProductCard ──────────────────────────────────────────────

@Composable
fun ProductCard(product: Product, compact: Boolean = false, onClick: () -> Unit) {
    val width = if (compact) 160.dp else Dp.Unspecified
    Card(
        onClick    = onClick,
        modifier   = if (compact) Modifier.width(width) else Modifier.fillMaxWidth(),
        shape      = RoundedCornerShape(14.dp),
        elevation  = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors     = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(if (compact) 130.dp else 160.dp)) {
                AsyncImage(
                    model          = product.image,
                    contentDescription = product.name,
                    contentScale   = ContentScale.Crop,
                    modifier       = Modifier.fillMaxSize()
                )
                if (product.badge != null) {
                    Surface(
                        color  = badgeColor(product.badge),
                        shape  = RoundedCornerShape(topStart = 14.dp, bottomEnd = 10.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            product.badge, color = Color.White,
                            fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                if (!product.inStock) {
                    Box(
                        Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Out of Stock",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    product.category.replaceFirstChar { it.uppercase() },
                    fontSize = 10.sp, color = Color.Blue, fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) { i ->
                        Text(if (i < product.rating.toInt()) "★" else "☆", fontSize = 11.sp, color = Color(0xFFF6AD55))
                    }
                    Text("(${product.reviews})", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("£${String.format("%.2f", product.price)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(" /${product.unit}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        }
    }
}

private fun badgeColor(badge: String): Color = when (badge) {
    "Sale"        -> Color(0xFFEE1C25)
    "Organic"     -> Color(0xFF2F855A)
    "New"         -> Color(0xFF005EB8)
    "Free Range"  -> Color(0xFF2F855A)
    "Wild Caught" -> Color(0xFF005EB8)
    "Vegan"       -> Color(0xFF2F855A)
    "Artisan"     -> Color(0xFFC77B3A)
    else          -> Color(0xFF005EB8)
}

@Composable
fun ShimmerProductGridCard() {
    Box(
        modifier = Modifier.fillMaxWidth().height(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}