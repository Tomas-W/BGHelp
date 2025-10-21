package com.example.bghelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.bghelp.ui.MainScreen
import com.example.bghelp.ui.theme.ComposablesTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(scrim = Color.Black.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.Black.toArgb())
        )

        setContent {
            ComposablesTheme {
                MainScreen()
            }
        }
    }
}


