package com.example.onlineshopping.ui.model

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loginState: LoginResult = LoginResult.Idle
)

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}
