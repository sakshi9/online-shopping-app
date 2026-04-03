package com.example.onlineshopping.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.data.model.User
import com.example.onlineshopping.ui.util.Conversion.Companion.formatValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToShop: () -> Unit
) {
    val user = remember {
        User(
            "u1", "Sakshi Gupta", "sakshi.libra9@gmail.com",
            "6342 8819 2231 0042", 1250,
            Address("Whitefield", "Bengaluru", "560045")
        )
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Profile", "Orders", "Clubcard")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Avatar header
            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp)
                    .background(Brush.linearGradient(listOf(Color.Blue, Color.Cyan)))
            ) {
                Row(modifier = Modifier.align(Alignment.TopStart).padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.name.split(" ").map { it.first() }.joinToString(""),
                            color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(user.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(user.email, color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Surface(color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(99.dp)) {
                            Text("${user.points} Clubcard pts", color = Color.White, fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i },
                        text = { Text(title, fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal) },
                        selectedContentColor = Color.Blue, unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }

            when (selectedTab) {
                0 -> ProfileTab(user = user)
                1 -> OrdersTab(onShop = onNavigateToShop)
                2 -> ClubcardTab(user = user)
            }
        }
    }
}

@Composable
private fun ProfileTab(user: User) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Personal Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ProfileInfoCard {
            ProfileRow(Icons.Default.Person, "Full Name", user.name)
            HorizontalDivider()
            ProfileRow(Icons.Default.Email, "Email", user.email)
            HorizontalDivider()
            ProfileRow(Icons.Default.Phone, "Phone", "+91 9650423342")
        }
        Spacer(Modifier.height(4.dp))
        Text("Delivery Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ProfileInfoCard {
            ProfileRow(Icons.Default.Home, "Address", user.address.line1)
            HorizontalDivider()
            ProfileRow(Icons.Default.LocationCity, "City", user.address.city)
            HorizontalDivider()
            ProfileRow(Icons.Default.PinDrop, "Postcode", user.address.postcode)
        }
        Spacer(Modifier.height(8.dp))
        /*OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(99.dp)
        ) {
            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Edit Profile")
        }
        TextButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
        ) {
            Icon(Icons.Default.Logout, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Sign Out")
        }*/
    }
}

@Composable
private fun OrdersTab(onShop: () -> Unit) {
    val mockOrders = remember {
        listOf(
            Triple("TES-A3F9K2", "3 days ago", "₹240.53"),
            Triple("TES-B7XQ01", "10 days ago", "₹400.20"),
            Triple("TES-C2MR55", "1 month ago", "₹180.75"),
        )
    }
    if (mockOrders.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("📦", fontSize = 48.sp)
                Text("No orders yet", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onShop, colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)) {
                    Text("Start Shopping")
                }
            }
        }
    } else {
        LazyColumnForOrders(mockOrders)
    }
}

@Composable
private fun LazyColumnForOrders(orders: List<Triple<String, String, String>>) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        orders.forEach { (id, date, total) ->
            Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(id, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Surface(color = Color.Green.copy(alpha = 0.12f), shape = RoundedCornerShape(99.dp)) {
                            Text("Delivered", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(total, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.Blue)
                        Spacer(Modifier.height(8.dp))
                        //TextButton(onClick = {}) { Text("Reorder", color = Color.Blue, fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubcardTab(user: User) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card visual
        Box(
            modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF1A365D), Color.Blue)))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text("S", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(4.dp))
                Text("Clubcard", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
            Text(user.clubcard, color = Color.White, fontSize = 16.sp,
                fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.BottomStart),
                letterSpacing = 2.sp)
            Text(user.name, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomEnd))
        }

        // Points
        Card(shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Available Points", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text("${user.points}", fontSize = 44.sp, fontWeight = FontWeight.Black, color = Color.Blue)
                Text("≈ ${formatValue(user.points / 100.0)} voucher value",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(99.dp)
                ) { Text("Redeem Points", fontWeight = FontWeight.Bold) }
            }
        }

        // How to earn
        Text("How to earn more points", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        listOf(
            "🛒" to "Earn 1 point per ₹1 spent in store",
            "📱" to "Double points on app orders",
            "🎯" to "Bonus points on selected products",
            "🎂" to "Birthday bonus — 200 points"
        ).forEach { (icon, text) ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(icon, fontSize = 20.sp)
                Text(text, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(4.dp), content = content)
    }
}

@Composable
private fun ProfileRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, null, tint = Color.Blue, modifier = Modifier.size(20.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}