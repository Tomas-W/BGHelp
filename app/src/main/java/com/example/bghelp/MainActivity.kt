package com.example.bghelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.example.bghelp.ui.MainScreen
import com.example.bghelp.ui.theme.BGHelpTheme
import com.example.bghelp.ui.theme.BackgroundGrey
import com.example.bghelp.ui.theme.MainBlue
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            
            SideEffect {
                // set NavigationBar color - StatusBar set by TopBar
                systemUiController.setNavigationBarColor(
                    color = BackgroundGrey,
                    darkIcons = true
                )
            }
            
            BGHelpTheme {
                MainScreen()
            }
        }
    }
}
