package com.example.onlineshopping.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.onlineshopping.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderStore: OrderStore
) : ViewModel(){

    val order: StateFlow<Order?> = orderStore.order

    fun clearOrder() = orderStore.clear()
}