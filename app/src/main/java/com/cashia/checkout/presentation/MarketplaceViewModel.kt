package com.cashia.checkout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashia.checkout.model.BuyerProfile
import com.cashia.checkout.model.CarbonCreditListing
import com.cashia.checkout.model.Crop
import com.cashia.checkout.model.Farmer
import com.cashia.checkout.model.PaymentResponse
import com.cashia.checkout.repository.MarketplaceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MarketplaceViewModel : ViewModel() {

    private val marketplaceRepository = MarketplaceRepository.getInstance()
//    private val paymentRepository = PaymentRepository.getInstance()

    val listings = marketplaceRepository.listings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cartItems = marketplaceRepository.cartItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val buyerProfile = marketplaceRepository.buyerProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filteredListings = MutableStateFlow<List<CarbonCreditListing>>(emptyList())
    val filteredListings = _filteredListings.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState = _paymentState.asStateFlow()

    init {
        viewModelScope.launch {
            listings.collect { allListings ->
                if (_searchQuery.value.isEmpty()) {
                    _filteredListings.value = allListings
                }
            }
        }
    }

    fun registerFarmer(
        name: String,
        farmSize: Double,
        crops: List<Crop>,
        location: String,
        email: String,
        phoneNumber: String?
    ) {
        val farmer = Farmer(
            name = name,
            farmSizeHectares = farmSize,
            crops = crops,
            location = location,
            email = email,
            phoneNumber = phoneNumber
        )
        marketplaceRepository.registerFarmer(farmer)
    }

    fun addToCart(listing: CarbonCreditListing, credits: Double) {
        marketplaceRepository.addToCart(listing, credits)
    }

    fun removeFromCart(cartItemId: String) {
        marketplaceRepository.removeFromCart(cartItemId)
    }

    fun updateCartItemQuantity(cartItemId: String, newQuantity: Double) {
        marketplaceRepository.updateCartItemQuantity(cartItemId, newQuantity)
    }

    fun clearCart() {
        marketplaceRepository.clearCart()
    }

    fun getCartTotal(): Double {
        return marketplaceRepository.getCartTotal()
    }

    fun getCartItemCount(): Int {
        return marketplaceRepository.getCartItemCount()
    }

    fun searchListings(query: String) {
        _searchQuery.value = query
        _filteredListings.value = marketplaceRepository.searchListings(query)
    }

    fun setBuyerProfile(profile: BuyerProfile) {
        marketplaceRepository.setBuyerProfile(profile)
    }

//    fun processCheckout() {
//        viewModelScope.launch {
//            _paymentState.value = PaymentState.Loading
//
//            val items = cartItems.value
//            val buyer = buyerProfile.value
//
//            if (items.isEmpty()) {
//                _paymentState.value = PaymentState.Error("Cart is empty")
//                return@launch
//            }
//
//            if (buyer == null) {
//                _paymentState.value = PaymentState.Error("Buyer profile not set")
//                return@launch
//            }
//
//            val result = paymentRepository.processPayment(items, buyer.email)
//
//            result.fold(
//                onSuccess = { response ->
//                    if (response.success) {
//                        _paymentState.value = PaymentState.Success(response)
//                        clearCart()
//                    } else {
//                        _paymentState.value = PaymentState.Error(response.message)
//                    }
//                },
//                onFailure = { error ->
//                    _paymentState.value = PaymentState.Error(
//                        error.message ?: "Payment processing failed"
//                    )
//                }
//            )
//        }
//    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }
}

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val response: PaymentResponse) : PaymentState()
    data class Error(val message: String) : PaymentState()
}