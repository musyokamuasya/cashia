package com.cashia.checkout.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cashia.checkout.presentation.components.ListingCard
import com.cashia.checkout.presentation.components.PurchaseDialog
import com.cashia.checkout.model.CarbonCreditListing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(viewModel: MarketplaceViewModel) {
    val filteredListings by viewModel.filteredListings.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showPurchaseDialog by remember { mutableStateOf(false) }
    var selectedListing by remember { mutableStateOf<CarbonCreditListing?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Carbon Credits Marketplace")
                        Text(
                            "Support sustainable farming",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchListings(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by farmer, location, or crop...") },
                leadingIcon = { Icon(Icons.Filled.Search, "Search") },
                singleLine = true
            )

            // Listings
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredListings) { listing ->
                    ListingCard(
                        listing = listing,
                        onBuyClick = {
                            selectedListing = listing
                            showPurchaseDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showPurchaseDialog && selectedListing != null) {
        PurchaseDialog(
            listing = selectedListing!!,
            onDismiss = { showPurchaseDialog = false },
            onConfirm = { credits ->
                viewModel.addToCart(selectedListing!!, credits)
                snackbarMessage = "Added ${String.format("%.2f", credits)} credits to cart"
                showPurchaseDialog = false
            }
        )
    }
}