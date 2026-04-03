package com.example.onlineshopping.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopping.ui.model.LoginResult
import com.example.onlineshopping.ui.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(v: String) =
        _state.update { it.copy(email = v, loginState = LoginResult.Idle) }

    fun onPasswordChange(v: String) =
        _state.update { it.copy(password = v, loginState = LoginResult.Idle) }

    fun login() {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.update {
                it.copy(loginState = LoginResult.Error("Please enter email and password"))
            }
            return
        }
        if (!s.email.contains("@")) {
            _state.update {
                it.copy(loginState = LoginResult.Error("Please enter a valid email address"))
            }
            return
        }
        if (s.password.length < 4) {
            _state.update {
                it.copy(loginState = LoginResult.Error("Password must be at least 4 characters"))
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loginState = LoginResult.Loading) }
            delay(900)
            _state.update { it.copy(loginState = LoginResult.Success) }
        }
    }

    fun guestLogin() {
        viewModelScope.launch {
            _state.update { it.copy(loginState = LoginResult.Loading) }
            delay(400)
            _state.update { it.copy(loginState = LoginResult.Success) }
        }
    }
}