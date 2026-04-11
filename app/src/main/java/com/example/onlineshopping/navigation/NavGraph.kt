package com.example.onlineshopping.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Store

sealed class NavGraph(val route: String) {
    object Home : NavGraph("home")
    object Shop : NavGraph("shop") {
        const val ARG_CATEGORY = "category"
        const val ARG_SEARCH = "search"
        val fullRoute = "$route?$ARG_CATEGORY={$ARG_CATEGORY}&$ARG_SEARCH={$ARG_SEARCH}"

        fun createRoute(category: String = "all", search: String = "") =
            "$route?$ARG_CATEGORY=$category&$ARG_SEARCH=$search"
    }

    object Product : NavGraph("product") {
        const val ARG_ID = "productId"
        val fullRoute = "$route/{$ARG_ID}"
        fun createRoute(id: String) = "$route/$id"
    }
    object Cart : NavGraph("cart")
    object Checkout : NavGraph("checkout")
    object OrderDone : NavGraph("order") {
        const val ARG_ID = "orderId"
        val fullRoute = "$route/{$ARG_ID}"
        fun createRoute(id: String) = "$route/$id"
    }
    object Login     : NavGraph("login")
    object Account   : NavGraph("account")
}

val BOTTOM_NAV_ITEMS = listOf(
    BottomNavItem(
        route = NavGraph.Home.route,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = NavGraph.Shop.route,
        label = "Shop",
        selectedIcon = Icons.Filled.Store,
        unselectedIcon = Icons.Outlined.Store
    ),
    BottomNavItem(
        route = NavGraph.Cart.route,
        label = "Cart",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    ),
    BottomNavItem(
        route = NavGraph.Account.route,
        label = "Account",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)