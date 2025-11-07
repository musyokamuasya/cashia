package com.cashia.checkout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cashia.checkout.presentation.CartScreen
import com.cashia.checkout.presentation.FarmerRegistrationScreen
import com.cashia.checkout.presentation.MarketplaceScreen
import com.cashia.checkout.presentation.MarketplaceViewModel
import com.cashia.checkout.presentation.ProfileScreen
import com.cashia.checkout.ui.theme.CashiaCheckoutTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MarketplaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CashiaCheckoutTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Marketplace : Screen("marketplace", "Marketplace", Icons.Filled.ShoppingBag)
    object Register : Screen("register", "Register", Icons.Filled.Add)
    object Cart : Screen("cart", "Cart", Icons.Filled.ShoppingCart)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MarketplaceViewModel) {
    val navController = rememberNavController()
    val cartItems by viewModel.cartItems.collectAsState()

    val items = listOf(
        Screen.Marketplace,
        Screen.Register,
        Screen.Cart,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (screen == Screen.Cart && cartItems.isNotEmpty()) {
                                        Badge {
                                            Text(cartItems.size.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(screen.icon, contentDescription = screen.title)
                            }
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Marketplace.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Marketplace.route) {
                MarketplaceScreen(viewModel = viewModel)
            }
            composable(Screen.Register.route) {
                FarmerRegistrationScreen(viewModel = viewModel)
            }
            composable(Screen.Cart.route) {
                CartScreen(
                    onNavigateToProfile = {
                        navController.navigate(Screen.Cart.route)
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }
        }
    }
}