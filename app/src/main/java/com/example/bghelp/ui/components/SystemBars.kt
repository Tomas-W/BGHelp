package com.example.bghelp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SystemBars() {
    val systemUiController = rememberSystemUiController()
    val backgroundColor = MaterialTheme.colorScheme.background
    val statusBarColor = backgroundColor
    val navigationBarColor = backgroundColor
    val useDarkIcons = backgroundColor.luminance() > 0.5f

    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor, darkIcons = useDarkIcons)
        systemUiController.setNavigationBarColor(navigationBarColor, darkIcons = useDarkIcons)
    }
}