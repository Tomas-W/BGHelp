package com.example.bghelp.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.bghelp.ui.theme.BGHelpTheme

@Composable
fun PreviewScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BGHelpTheme {
        val backgroundColor = MaterialTheme.colorScheme.background
        val useDarkIcons = backgroundColor.luminance() > 0.5f
        val view = LocalView.current
        
        // Set status/navigation bar colors
        SideEffect {
            val window = when (val context = view.context) {
                is android.app.Activity -> context.window
                is android.content.ContextWrapper -> {
                    (context.baseContext as? android.app.Activity)?.window
                }
                else -> null
            }
            
            window?.let {
                // Transparent status bar
                @Suppress("DEPRECATION")
                it.statusBarColor = android.graphics.Color.TRANSPARENT
                
                // Background colored navigation bar
                @Suppress("DEPRECATION")
                it.navigationBarColor = backgroundColor.toArgb()
                
                // Allow custom colors
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    it.isNavigationBarContrastEnforced = false
                }
                
                // Icon colors based on background luminance
                val windowInsetsController = WindowCompat.getInsetsController(it, it.decorView)
                windowInsetsController.isAppearanceLightStatusBars = useDarkIcons
                windowInsetsController.isAppearanceLightNavigationBars = useDarkIcons
            }
        }
        
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            content()
        }
    }
}

