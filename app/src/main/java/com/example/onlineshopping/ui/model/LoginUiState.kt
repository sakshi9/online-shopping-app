package com.example.onlineshopping.ui.model

data class LoginUiState(
    val email: String    = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String?   = null,
    val isLoggedIn: Boolean = false
)
