package com.example.onlineshopping.ui.model

import com.example.onlineshopping.data.model.Order
import com.example.onlineshopping.ui.viewModel.DELIVERY_SLOTS

data class CheckoutUiState(
    val line1: String          = "Whitefield",
    val city: String           = "Bengaluru",
    val pincode: String       = "560045",
    val selectedSlot: DeliverySlot = DELIVERY_SLOTS.first(),
    val paymentMethod: String  = "card",
    val cardNumber: String     = "",
    val cardName: String       = "",
    val cardExpiry: String     = "",
    val cardCvv: String        = "",
    val isPlacingOrder: Boolean= false,
    val order: Order?          = null,
    val error: String?         = null,
    val subtotal: Double       = 0.0,
    val deliveryFee: Double    = 20.0,
    val total: Double          = 0.0
)
