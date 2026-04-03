package com.example.onlineshopping.ui.viewModel

import com.example.onlineshopping.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderStore @Inject constructor() {

    private val _order = MutableStateFlow<Order?>(null)
    val order: StateFlow<Order?> = _order.asStateFlow()

    fun set(order: Order) { _order.value = order }
    fun clear()           { _order.value = null  }
}