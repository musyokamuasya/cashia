package com.cashia.checkout.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cashia.checkout.presentation.components.CartItemCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToProfile: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val buyerProfile by viewModel.buyerProfile.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()

    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showProfileRequiredDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var transactionId by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is PaymentState.Success -> {
                transactionId = state.response.transactionId
                successMessage = state.response.message
                showSuccessDialog = true
            }

            is PaymentState.Error -> {
                errorMessage = state.message
                showErrorDialog = true
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Shopping Cart")
                        Text(
                            "Review your carbon credit purchases",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Your cart is empty",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Browse the marketplace to add carbon credits",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onRemove = { viewModel.removeFromCart(cartItem.id) },
                                onQuantityChange = { newQuantity ->
                                    viewModel.updateCartItemQuantity(cartItem.id, newQuantity)
                                }
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Total: $${String.format("%.2f", viewModel.getCartTotal())}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = {
                                    if (buyerProfile == null) {
                                        showProfileRequiredDialog = true
                                    } else {
                                        showCheckoutDialog = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = paymentState !is PaymentState.Loading
                            ) {
                                if (paymentState is PaymentState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text(
                                        "Proceed to Checkout",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Checkout Confirmation Dialog
    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = { Text("Confirm Purchase") },
            text = {
                Text(
                    "You are about to purchase carbon credits from ${cartItems.size} farmer(s).\n\n" +
                            "Total Amount: $${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    viewModel.getCartTotal()
                                )
                            }"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutDialog = false
//                        viewModel.processCheckout()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Profile Required Dialog
    if (showProfileRequiredDialog) {
        AlertDialog(
            onDismissRequest = { showProfileRequiredDialog = false },
            title = { Text("Profile Required") },
            text = { Text("Please set up your buyer profile before checkout.") },
            confirmButton = {
                Button(
                    onClick = {
                        showProfileRequiredDialog = false
                        onNavigateToProfile()
                    }
                ) {
                    Text("Go to Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileRequiredDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetPaymentState()
            },
            title = { Text("Payment Successful!") },
            text = {
                Text(
                    "$successMessage\n\n" +
                            "Transaction ID: $transactionId\n\n" +
                            "Thank you for supporting sustainable farming!"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetPaymentState()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.resetPaymentState()
            },
            title = { Text("Payment Failed") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog = false
                        viewModel.resetPaymentState()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}