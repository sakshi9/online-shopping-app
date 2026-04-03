package com.example.onlineshopping.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineshopping.ui.util.Conversion.Companion.formatValue
import com.example.onlineshopping.ui.viewModel.CheckoutViewModel
import com.example.onlineshopping.ui.viewModel.DELIVERY_SLOTS

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderPlaced: (orderId: String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.order) {
        state.order?.let { onOrderPlaced(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Delivery address
            CheckoutSection(title = "📍 Delivery Address") {
                CheckoutTextField(
                    "Address Line 1", state.line1, viewModel::onLine1Change,
                    KeyboardType.Text, Icons.Default.Home
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CheckoutTextField(
                        "City",
                        state.city,
                        viewModel::onCityChange,
                        KeyboardType.Text,
                        Icons.Default.LocationCity,
                        modifier = Modifier.weight(1f)
                    )
                    CheckoutTextField(
                        "Pincode", state.pincode, viewModel::onPostcodeChange,
                        KeyboardType.Text, Icons.Default.PinDrop, modifier = Modifier.weight(1f)
                    )
                }
            }

            // Delivery slot
            CheckoutSection(title = "🚚 Delivery Slot") {
                DELIVERY_SLOTS.forEach { slot ->
                    SlotOption(
                        time = slot.time,
                        type = slot.type,
                        isSelected = state.selectedSlot == slot,
                        onClick = { viewModel.onSlotChange(slot) }
                    )
                }
            }

            // Payment
            CheckoutSection(title = "💳 Payment Method") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PaymentOption(
                        "card",
                        "💳 Card",
                        state.paymentMethod == "card"
                    ) { viewModel.onPaymentMethodChange("card") }
                    PaymentOption(
                        "paypal",
                        "🅿️ PayPal",
                        state.paymentMethod == "paypal"
                    ) { viewModel.onPaymentMethodChange("paypal") }
                    PaymentOption(
                        "clubcard",
                        "🎴 Clubcard",
                        state.paymentMethod == "clubcard"
                    ) { viewModel.onPaymentMethodChange("clubcard") }
                }
                if (state.paymentMethod == "card") {
                    Spacer(Modifier.height(4.dp))
                    CheckoutTextField(
                        "Card Number", state.cardNumber, viewModel::onCardNumberChange,
                        KeyboardType.Number, Icons.Default.CreditCard
                    )
                    CheckoutTextField(
                        "Name on Card", state.cardName, viewModel::onCardNameChange,
                        KeyboardType.Text, Icons.Default.Person
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheckoutTextField(
                            "MM/YY",
                            state.cardExpiry,
                            viewModel::onCardExpiryChange,
                            KeyboardType.Number,
                            Icons.Default.DateRange,
                            modifier = Modifier.weight(1f)
                        )
                        CheckoutTextField(
                            "CVV", state.cardCvv, viewModel::onCardCvvChange,
                            KeyboardType.Number, Icons.Default.Lock, modifier = Modifier.weight(1f),
                            isPassword = true
                        )
                    }
                }
            }

            // Order summary
            CheckoutSection(title = "🧾 Order Summary") {
                SummaryLine("Subtotal", "₹${formatValue(state.subtotal)}")
                SummaryLine(
                    "Delivery",
                    if (state.deliveryFee == 0.0) "FREE" else formatValue(state.deliveryFee),
                    valueColor = if (state.deliveryFee == 0.0) Color.Green else MaterialTheme.colorScheme.onSurface
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SummaryLine("Total to Pay", formatValue(state.total), isBold = true)
            }

            // Error
            state.error?.let { errorMsg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            null,
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(errorMsg, color = Color.Red, fontSize = 13.sp)
                    }
                }
            }

            // Place order button
            Button(
                onClick = viewModel::placeOrder,
                enabled = !state.isPlacingOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                shape = RoundedCornerShape(99.dp)
            ) {
                if (state.isPlacingOrder) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Placing Order…", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                } else {
                    Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Place Order · ${formatValue(state.total)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CheckoutSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
private fun CheckoutTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    icon: ImageVector,
    modifier: Modifier = Modifier.fillMaxWidth(),
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(
                icon, null,
                modifier = Modifier.size(18.dp)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        singleLine = true, shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}

@Composable
private fun PaymentOption(id: String, label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) Color.Blue else MaterialTheme.colorScheme.outline
        ),
        color = if (selected) Color.Blue.copy(alpha = 0.07f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.defaultMinSize(minHeight = 48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.Blue else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SlotOption(
    time: String,
    type: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            .background(if (isSelected) Color.Blue.copy(alpha = 0.07f) else Color.Transparent)
            .clickable { onClick() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
            )
            Column {
                Text(
                    time,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp
                )
                Text(
                    type,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        if (isSelected) Text(
            "Selected",
            color = Color.Blue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isBold) 16.sp else 14.sp
        )
        Text(
            value, fontWeight = if (isBold) FontWeight.Black else FontWeight.SemiBold,
            fontSize = if (isBold) 18.sp else 14.sp, color = valueColor
        )
    }
}