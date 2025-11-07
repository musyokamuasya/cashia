package com.cashia.checkout.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashia.checkout.model.Crop
import com.cashia.checkout.model.CropType
import java.util.Locale

data class CropInput(
    val id: Int = (0..10000).random(),
    var cropType: CropType = CropType.CORN,
    var area: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerRegistrationScreen(viewModel: MarketplaceViewModel) {
    var farmerName by remember { mutableStateOf("") }
    var farmSize by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var cropInputs by remember { mutableStateOf(listOf(CropInput())) }
    var estimatedCredits by remember { mutableStateOf(0.0) }

    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    LaunchedEffect(cropInputs) {
        estimatedCredits = cropInputs.sumOf { input ->
            val area = input.area.toDoubleOrNull() ?: 0.0
            if (area > 0) {
                Crop(input.cropType, area).calculateCarbonCredits()
            } else 0.0
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Register Your Farm")
                        Text(
                            "Enter details to generate carbon credits",
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
            OutlinedTextField(
                value = farmerName,
                onValueChange = { farmerName = it },
                label = { Text("Farmer Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = farmSize,
                onValueChange = { farmSize = it },
                label = { Text("Farm Size (hectares)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
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

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                "Crops Planted",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            cropInputs.forEachIndexed { index, cropInput ->
                CropInputCard(
                    cropInput = cropInput,
                    onCropTypeChange = { newType ->
                        cropInputs = cropInputs.toMutableList().apply {
                            this[index] = cropInput.copy(cropType = newType)
                        }
                    },
                    onAreaChange = { newArea ->
                        cropInputs = cropInputs.toMutableList().apply {
                            this[index] = cropInput.copy(area = newArea)
                        }
                    },
                    onRemove = if (cropInputs.size > 1) {
                        {
                            cropInputs = cropInputs.toMutableList().apply {
                                removeAt(index)
                            }
                        }
                    } else null
                )
            }

            OutlinedButton(
                onClick = {
                    cropInputs = cropInputs + CropInput()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, "Add")
                Spacer(Modifier.width(8.dp))
                Text("Add Another Crop")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "Estimated Carbon Credits: ${String.format(Locale.US, "%.2f", estimatedCredits)} tons CO₂",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Button(
                onClick = {
                    if (validateAndRegister(
                            farmerName, farmSize, location, email,
                            phoneNumber, cropInputs, viewModel
                        )
                    ) {
                        snackbarMessage = "Farm registered successfully!"
                        showSnackbar = true

                        // Clear form
                        farmerName = ""
                        farmSize = ""
                        location = ""
                        email = ""
                        phoneNumber = ""
                        cropInputs = listOf(CropInput())
                    } else {
                        snackbarMessage = "Please fill in all required fields correctly"
                        showSnackbar = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Register Farm", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropInputCard(
    cropInput: CropInput,
    onCropTypeChange: (CropType) -> Unit,
    onAreaChange: (String) -> Unit,
    onRemove: (() -> Unit)?
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Crop Details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (onRemove != null) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Filled.Delete, "Remove crop")
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = cropInput.cropType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Crop Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CropType.values().forEach { cropType ->
                        DropdownMenuItem(
                            text = { Text(cropType.displayName) },
                            onClick = {
                                onCropTypeChange(cropType)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = cropInput.area,
                onValueChange = onAreaChange,
                label = { Text("Area (hectares)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun validateAndRegister(
    name: String,
    farmSize: String,
    location: String,
    email: String,
    phoneNumber: String,
    cropInputs: List<CropInput>,
    viewModel: MarketplaceViewModel
): Boolean {
    if (name.isBlank() || farmSize.isBlank() || location.isBlank() || email.isBlank()) {
        return false
    }

    val farmSizeDouble = farmSize.toDoubleOrNull() ?: return false
    if (farmSizeDouble <= 0) return false

    val crops = cropInputs.mapNotNull { input ->
        val area = input.area.toDoubleOrNull()
        if (area != null && area > 0) {
            Crop(input.cropType, area)
        } else null
    }

    if (crops.isEmpty()) return false

    val totalCropArea = crops.sumOf { it.areaHectares }
    if (totalCropArea > farmSizeDouble) return false

    viewModel.registerFarmer(
        name = name,
        farmSize = farmSizeDouble,
        crops = crops,
        location = location,
        email = email,
        phoneNumber = phoneNumber.takeIf { it.isNotBlank() }
    )

    return true
}