package com.cashia.checkout.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashia.checkout.model.BuyerProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MarketplaceViewModel) {
    val buyerProfile by viewModel.buyerProfile.collectAsState()

    var buyerName by remember { mutableStateOf(buyerProfile?.name ?: "") }
    var companyName by remember { mutableStateOf(buyerProfile?.companyName ?: "") }
    var email by remember { mutableStateOf(buyerProfile?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(buyerProfile?.phoneNumber ?: "") }

    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(buyerProfile) {
        buyerProfile?.let { profile ->
            buyerName = profile.name
            companyName = profile.companyName
            email = profile.email
            phoneNumber = profile.phoneNumber ?: ""
        }
    }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar("Profile saved successfully!")
            showSnackbar = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Buyer Profile")
                        Text(
                            "Set up your profile to purchase credits",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (buyerProfile != null) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Text(
                    text = if (buyerProfile != null) {
                        "Profile Status: Active ✓"
                    } else {
                        "Profile Status: Not Set"
                    },
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (buyerProfile != null) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }

            OutlinedTextField(
                value = buyerName,
                onValueChange = { buyerName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateAndSaveProfile(
                            buyerName, companyName, email, phoneNumber, viewModel
                        )
                    ) {
                        showSnackbar = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save Profile", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private fun validateAndSaveProfile(
    name: String,
    companyName: String,
    email: String,
    phoneNumber: String,
    viewModel: MarketplaceViewModel
): Boolean {
    if (name.isBlank() || companyName.isBlank() || email.isBlank()) {
        return false
    }

    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return false
    }

    val profile = BuyerProfile(
        name = name,
        email = email,
        companyName = companyName,
        phoneNumber = phoneNumber.takeIf { it.isNotBlank() }
    )

    viewModel.setBuyerProfile(profile)
    return true
}