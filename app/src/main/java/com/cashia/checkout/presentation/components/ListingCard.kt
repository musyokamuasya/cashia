package com.cashia.checkout.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashia.checkout.model.CarbonCreditListing
import java.util.Locale

@Composable
fun ListingCard(
    listing: CarbonCreditListing,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = listing.farmer.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = listing.farmer.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Divider()

            // Farm Details
            Text(
                text = "Farm Size: ${listing.farmer.farmSizeHectares} hectares",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Crops: ${listing.getCropSummary()}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Credits Info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${String.format("%.2f", listing.availableCredits)} credits",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$${String.format("%.2f", listing.pricePerCredit)} per credit",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Text(
                            text = "$${String.format("%.2f", listing.getTotalPrice())}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Buy Button
            Button(
                onClick = onBuyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buy Credits", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
fun PurchaseDialog(
    listing: CarbonCreditListing,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var creditsAmount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Purchase Carbon Credits") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("From: ${listing.farmer.name}")
                Text("Available: ${String.format(Locale.US, "%.2f", listing.availableCredits)} credits")
                Text("Price: $${String.format(Locale.US, "%.2f", listing.pricePerCredit)}/credit")

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = creditsAmount,
                    onValueChange = {
                        creditsAmount = it
                        errorMessage = null
                    },
                    label = { Text("Number of Credits") },
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val credits = creditsAmount.toDoubleOrNull()
                    when {
                        credits == null || credits <= 0 -> {
                            errorMessage = "Please enter a valid amount"
                        }
                        credits > listing.availableCredits -> {
                            errorMessage = "Not enough credits available"
                        }
                        else -> {
                            onConfirm(credits)
                        }
                    }
                }
            ) {
                Text("Add to Cart")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}