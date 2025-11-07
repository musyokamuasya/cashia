package com.cashia.checkout.repository

import com.cashia.checkout.model.BuyerProfile
import com.cashia.checkout.model.CarbonCreditListing
import com.cashia.checkout.model.CartItem
import com.cashia.checkout.model.Crop
import com.cashia.checkout.model.CropType
import com.cashia.checkout.model.Farmer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MarketplaceRepository {

    private val _farmers = MutableStateFlow<List<Farmer>>(emptyList())
    val farmers: StateFlow<List<Farmer>> = _farmers.asStateFlow()

    private val _listings = MutableStateFlow<List<CarbonCreditListing>>(emptyList())
    val listings: StateFlow<List<CarbonCreditListing>> = _listings.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _buyerProfile = MutableStateFlow<BuyerProfile?>(null)
    val buyerProfile: StateFlow<BuyerProfile?> = _buyerProfile.asStateFlow()

    init {
        initializeSampleData()
    }

    private fun initializeSampleData() {
        val sampleFarmers = listOf(
            Farmer(
                name = "John Kamau",
                farmSizeHectares = 50.0,
                crops = listOf(
                    Crop(CropType.CORN, 30.0),
                    Crop(CropType.WHEAT, 20.0)
                ),
                location = "Nakuru, Kenya",
                email = "john.kamau@example.com",
                phoneNumber = "+254 712 345 678"
            ),
            Farmer(
                name = "Sarah Wanjiru",
                farmSizeHectares = 75.0,
                crops = listOf(
                    Crop(CropType.COFFEE, 45.0),
                    Crop(CropType.VEGETABLES, 30.0)
                ),
                location = "Kiambu, Kenya",
                email = "sarah.wanjiru@example.com",
                phoneNumber = "+254 723 456 789"
            ),
            Farmer(
                name = "David Omondi",
                farmSizeHectares = 100.0,
                crops = listOf(
                    Crop(CropType.SUGARCANE, 80.0),
                    Crop(CropType.RICE, 20.0)
                ),
                location = "Kisumu, Kenya",
                email = "david.omondi@example.com",
                phoneNumber = "+254 734 567 890"
            )
        )

        _farmers.value = sampleFarmers

        val sampleListings = sampleFarmers.map { farmer ->
            val totalCredits = farmer.crops.sumOf { it.calculateCarbonCredits() }
            CarbonCreditListing(
                farmer = farmer,
                totalCredits = totalCredits,
                pricePerCredit = 15.0 + (Math.random() * 10.0),
                availableCredits = totalCredits,
                description = "Certified carbon credits from sustainable farming practices"
            )
        }

        _listings.value = sampleListings
    }

    fun registerFarmer(farmer: Farmer) {
        val currentFarmers = _farmers.value.toMutableList()
        currentFarmers.add(farmer)
        _farmers.value = currentFarmers

        val totalCredits = farmer.crops.sumOf { it.calculateCarbonCredits() }
        val newListing = CarbonCreditListing(
            farmer = farmer,
            totalCredits = totalCredits,
            pricePerCredit = 15.0 + (Math.random() * 10.0),
            availableCredits = totalCredits,
            description = "Certified carbon credits from sustainable farming practices"
        )

        val currentListings = _listings.value.toMutableList()
        currentListings.add(newListing)
        _listings.value = currentListings
    }

    fun addToCart(listing: CarbonCreditListing, credits: Double) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItemIndex = currentCart.indexOfFirst { it.listing.id == listing.id }

        if (existingItemIndex != -1) {
            val existingItem = currentCart[existingItemIndex]
            currentCart[existingItemIndex] = existingItem.copy(
                creditsPurchased = existingItem.creditsPurchased + credits
            )
        } else {
            currentCart.add(CartItem(listing = listing, creditsPurchased = credits))
        }

        _cartItems.value = currentCart
    }

    fun removeFromCart(cartItemId: String) {
        val currentCart = _cartItems.value.toMutableList()
        currentCart.removeAll { it.id == cartItemId }
        _cartItems.value = currentCart
    }

    fun updateCartItemQuantity(cartItemId: String, newQuantity: Double) {
        val currentCart = _cartItems.value.toMutableList()
        val index = currentCart.indexOfFirst { it.id == cartItemId }
        if (index != -1) {
            currentCart[index] = currentCart[index].copy(creditsPurchased = newQuantity)
            _cartItems.value = currentCart
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getCartTotal(): Double {
        return _cartItems.value.sumOf { it.getTotalPrice() }
    }

    fun getCartItemCount(): Int {
        return _cartItems.value.size
    }

    fun setBuyerProfile(profile: BuyerProfile) {
        _buyerProfile.value = profile
    }

    fun searchListings(query: String): List<CarbonCreditListing> {
        return if (query.isBlank()) {
            _listings.value
        } else {
            _listings.value.filter { listing ->
                listing.farmer.name.contains(query, ignoreCase = true) ||
                        listing.farmer.location.contains(query, ignoreCase = true) ||
                        listing.getCropSummary().contains(query, ignoreCase = true)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MarketplaceRepository? = null

        fun getInstance(): MarketplaceRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MarketplaceRepository().also { INSTANCE = it }
            }
        }
    }
}