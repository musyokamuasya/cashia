package com.cashia.checkout.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashia.checkout.model.CartItem
import java.util.Locale

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onRemove: () -> Unit,
    onQuantityChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with farmer name and remove button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cartItem.listing.farmer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cartItem.listing.getCropSummary(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, "Remove from cart")
                }
            }

            Divider()

            // Quantity and price controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${String.format(Locale.US, "%.2f", cartItem.creditsPurchased)} credits",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", cartItem.listing.pricePerCredit)}/credit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val newQuantity =
                                        (cartItem.creditsPurchased - 1.0).coerceAtLeast(1.0)
                                    if (newQuantity != cartItem.creditsPurchased) {
                                        onQuantityChange(newQuantity)
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.Remove, "Decrease")
                            }

                            VerticalDivider(
                                modifier = Modifier.height(24.dp)
                            )

                            IconButton(
                                onClick = {
                                    val newQuantity = (cartItem.creditsPurchased + 1.0)
                                        .coerceAtMost(cartItem.listing.availableCredits)
                                    if (newQuantity != cartItem.creditsPurchased) {
                                        onQuantityChange(newQuantity)
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.Add, "Increase")
                            }
                        }
                    }

                    Text(
                        text = "$${String.format(Locale.US, "%.2f", cartItem.getTotalPrice())}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}