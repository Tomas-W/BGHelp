package com.example.bghelp.ui.components

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat

@Composable
fun SystemBars(window: android.view.Window?) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val useDarkIcons = backgroundColor.luminance() > 0.5f

    window?.let {
        SideEffect {
            // Transparent status bar
            @Suppress("DEPRECATION") // Deprecated in Android 15+ but still works on Android 14 and below
            it.statusBarColor = android.graphics.Color.TRANSPARENT
            
            // Background colored navigation bar
            @Suppress("DEPRECATION") // Deprecated in Android 15+ but still works on Android 14 and below
            it.navigationBarColor = backgroundColor.toArgb()
            
            // Allow custom colors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.isNavigationBarContrastEnforced = false
            }
            
            // Icon colors
            val windowInsetsController = WindowCompat.getInsetsController(it, it.decorView)
            windowInsetsController.isAppearanceLightStatusBars = useDarkIcons
            windowInsetsController.isAppearanceLightNavigationBars = useDarkIcons
        }
    }
}