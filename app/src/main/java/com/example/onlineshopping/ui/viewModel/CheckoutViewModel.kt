package com.example.onlineshopping.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopping.data.model.Address
import com.example.onlineshopping.domain.usecase.GetCheckoutSummaryUseCase
import com.example.onlineshopping.domain.usecase.PlaceOrderUseCase
import com.example.onlineshopping.domain.usecase.ValidateAddressUseCase
import com.example.onlineshopping.ui.model.CheckoutUiState
import com.example.onlineshopping.ui.model.DeliverySlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

val DELIVERY_SLOTS = listOf(
    DeliverySlot("Today, 6pm – 7pm",      "Express"),
    DeliverySlot("Today, 8pm – 9pm",      "Standard"),
    DeliverySlot("Tomorrow, 9am – 10am", "Standard"),
)
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val validateAddressUseCase: ValidateAddressUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getCheckoutSummaryUseCase: GetCheckoutSummaryUseCase,
    private val orderStore: OrderStore
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getCheckoutSummaryUseCase().collect { summary ->
                _state.update {
                    it.copy(
                        subtotal = summary.subtotal,
                        deliveryFee = summary.deliveryFee,
                        total = summary.total
                    )
                }
            }
        }
    }

    fun onSlotChange(slot: DeliverySlot) = _state.update { it.copy(selectedSlot = slot) }
    fun onLine1Change(v: String) = _state.update { it.copy(line1 = v) }
    fun onCityChange(v: String) = _state.update { it.copy(city = v) }
    fun onPostcodeChange(v: String) = _state.update { it.copy(pincode = v) }
    fun onPaymentMethodChange(v: String) = _state.update { it.copy(paymentMethod = v) }
    fun onCardNumberChange(v: String) = _state.update { it.copy(cardNumber = v.take(16)) }
    fun onCardNameChange(v: String) = _state.update { it.copy(cardName = v) }
    fun onCardExpiryChange(v: String) = _state.update { it.copy(cardExpiry = v.take(5)) }
    fun onCardCvvChange(v: String) = _state.update { it.copy(cardCvv = v.take(3)) }

    fun placeOrder() {
        val s = _state.value

        val validation = validateAddressUseCase(s.line1, s.city, s.pincode)
        if (validation.isFailure) {
            _state.update { it.copy(error = validation.exceptionOrNull()?.message) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isPlacingOrder = true, error = null) }

            val result = placeOrderUseCase(
                address = Address(s.line1, s.city, s.pincode),
                paymentMethod = s.paymentMethod,
                selectedSlot = s.selectedSlot
            )

            result.onSuccess { order ->
                orderStore.set(order)
                _state.update { it.copy(order = order, isPlacingOrder = false) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message, isPlacingOrder = false) }
            }
        }
    }
}