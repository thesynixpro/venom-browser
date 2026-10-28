package com.venom.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.venom.browser.ui.VenomBrowserApp
import com.venom.browser.ui.theme.VenomBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VenomBrowserTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VenomBrowserApp()
                }
            }
        }
    }
}
