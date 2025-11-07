package com.cashia.checkout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cashia.checkout.presentation.MainScreen
import com.cashia.checkout.presentation.theme.CashiaCheckoutTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CashiaCheckoutTheme {
                MainScreen()
            }
        }
    }
}