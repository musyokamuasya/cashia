package com.cashia.checkout.model

import java.util.UUID

data class Farmer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val farmSizeHectares: Double,
    val crops: List<Crop>,
    val location: String,
    val email: String,
    val phoneNumber: String? = null,
    val imageUrl: String? = null,
    val registrationDate: Long = System.currentTimeMillis()
)

data class Crop(
    val type: CropType,
    val areaHectares: Double,
    val plantingDate: Long = System.currentTimeMillis()
) {
    fun calculateCarbonCredits(): Double {
        return areaHectares * type.carbonCreditsPerHectare * (0.8 + Math.random() * 0.4)
    }
}

enum class CropType(val displayName: String, val carbonCreditsPerHectare: Double) {
    CORN("Corn", 2.5),
    WHEAT("Wheat", 2.8),
    SOYBEANS("Soybeans", 3.2),
    RICE("Rice", 2.0),
    COFFEE("Coffee", 4.5),
    COCOA("Cocoa", 5.0),
    SUGARCANE("Sugarcane", 2.3),
    COTTON("Cotton", 2.1),
    VEGETABLES("Vegetables", 3.0),
    FRUITS("Fruits", 3.5),
    BARLEY("Barley", 2.6),
    OATS("Oats", 2.7)
}

data class CarbonCreditListing(
    val id: String = UUID.randomUUID().toString(),
    val farmer: Farmer,
    val totalCredits: Double,
    val pricePerCredit: Double,
    val availableCredits: Double,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getTotalPrice(): Double = availableCredits * pricePerCredit

    fun getCropSummary(): String {
        return farmer.crops.joinToString(", ") { "${it.type.displayName} (${it.areaHectares}ha)" }
    }
}

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val listing: CarbonCreditListing,
    val creditsPurchased: Double,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun getTotalPrice(): Double = creditsPurchased * listing.pricePerCredit
}

data class PaymentRequest(
    val items: List<PaymentItem>,
    val totalAmount: Double,
    val currency: String = "USD",
    val buyerEmail: String,
    val metadata: Map<String, String> = emptyMap()
)

data class PaymentItem(
    val name: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double
)

data class PaymentResponse(
    val success: Boolean,
    val transactionId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class BuyerProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val companyName: String,
    val phoneNumber: String? = null
)