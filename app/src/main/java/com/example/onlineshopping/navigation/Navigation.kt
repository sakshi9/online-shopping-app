package com.example.onlineshopping.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.ui.screen.AccountScreen
import com.example.onlineshopping.ui.screen.CartScreen
import com.example.onlineshopping.ui.screen.CheckoutScreen
import com.example.onlineshopping.ui.screen.HomeScreen
import com.example.onlineshopping.ui.screen.LoginScreen
import com.example.onlineshopping.ui.screen.OrderConfirmScreen
import com.example.onlineshopping.ui.screen.ProductDetailScreen
import com.example.onlineshopping.ui.screen.ShopScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(navController: NavHostController,
               innerPadding: PaddingValues, confirmedOrder: Order? = null) {


    NavHost(
        navController = navController,
        startDestination = NavGraph.Home.route,
        modifier = Modifier.padding(innerPadding)
    )
    {
        composable(NavGraph.Home.route) {
            HomeScreen(
                onNavigateToShop = { cat -> navController.navigate(NavGraph.Shop.createRoute(category = cat)) },
                onNavigateToProduct = { id -> navController.navigate(NavGraph.Product.createRoute(id)) }
            )
        }

        composable(
            route = NavGraph.Shop.fullRoute,
            arguments = listOf(
                navArgument(NavGraph.Shop.ARG_CATEGORY) { defaultValue = "all" },
                navArgument(NavGraph.Shop.ARG_SEARCH) { defaultValue = "" }
            )
        ) { backStack ->
            val category = backStack.arguments?.getString("category") ?: "all"
            val search = backStack.arguments?.getString("search") ?: ""
            ShopScreen(
                initialCategory = category,
                initialSearch = search,
                onProductClick = { id -> navController.navigate(NavGraph.Product.createRoute(id)) }
            )
        }

        composable(
            route = NavGraph.Product.fullRoute,
            arguments = listOf(navArgument(NavGraph.Product.ARG_ID) {
                type = NavType.StringType
            })
        ) {
            ProductDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigateToProduct = { id ->
                    navController.navigate(NavGraph.Product.createRoute(id)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCart = { navController.navigate(NavGraph.Cart.route) }
            )
        }

        composable(NavGraph.Cart.route) {
            CartScreen(
                onNavigateToShop = { navController.navigate(NavGraph.Shop.createRoute()) },
                onNavigateToCheckout = { navController.navigate(NavGraph.Checkout.route) }
            )
        }

        composable(NavGraph.Checkout.route) {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onOrderPlaced = { orderId ->
                    navController.navigate(NavGraph.OrderDone.createRoute(orderId)) {
                        popUpTo(NavGraph.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavGraph.OrderDone.fullRoute,
            arguments = listOf(
                navArgument(NavGraph.OrderDone.ARG_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStack ->
            OrderConfirmScreen(
                onContinueShopping = {
                    navController.navigate(NavGraph.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                },
                onViewAccount = { navController.navigate(NavGraph.Account.route) }
            )
        }

        composable(NavGraph.Login.route) {
            LoginScreen(onLoggedIn = { navController.popBackStack() })
        }

        composable(NavGraph.Account.route) {
            AccountScreen(
                onNavigateToLogin = { navController.navigate(NavGraph.Login.route) },
                onNavigateToShop = { navController.navigate("shop?category=all&search=") }
            )
        }
    }
}