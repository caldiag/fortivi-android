package com.gruporihappy.fortivi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gruporihappy.fortivi.ui.theme.FortiviTheme
import com.gruporihappy.fortivi.home.components.Home

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent() {
            enableEdgeToEdge()
            FortiviTheme() {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    ){
                    Home(this)
                }
            }
        }
    }
}
