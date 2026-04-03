package com.example.onlineshopping

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.navigation.BOTTOM_NAV_ITEMS
import com.example.onlineshopping.navigation.NavGraph
import com.example.onlineshopping.navigation.Navigation
import com.example.onlineshopping.ui.theme.OnlineShoppingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlineShoppingTheme {
                OnlineShoppingApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OnlineShoppingApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Screens where bottom nav is hidden
    val hideBottomNav = currentRoute?.startsWith("product/") == true ||
            currentRoute == NavGraph.Checkout.route ||
            currentRoute?.startsWith("order/") == true ||
            currentRoute == NavGraph.Login.route

    // Persisted order state (passed between checkout → confirm)
    var confirmedOrder by remember { mutableStateOf<Order?>(null) }

    Scaffold(
        bottomBar = {
            if (!hideBottomNav) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    BOTTOM_NAV_ITEMS.forEach { item ->
                        val selected = currentRoute == item.route ||
                                (item.route.startsWith("shop") && currentRoute?.startsWith("shop") == true)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    item.label
                                )
                            },
                            label = { Text(item.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Blue,
                                selectedTextColor = Color.Blue,
                                indicatorColor = Color.Blue.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Navigation(navController, innerPadding, confirmedOrder)
    }
}