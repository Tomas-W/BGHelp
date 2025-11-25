package com.example.bghelp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.bghelp.ui.theme.lTextBold
import com.example.bghelp.ui.theme.lTextDefault
import com.example.bghelp.ui.theme.lTextSemi

@Composable
fun selectedTextStyle(): TextStyle {
    return MaterialTheme.typography.lTextBold
}

@Composable
fun selectedDropdownStyle(): TextStyle {
    return MaterialTheme.typography.lTextSemi
}

@Composable
fun deselectedDropdownStyle(): TextStyle {
    return MaterialTheme.typography.lTextDefault
}

@Composable
fun deselectedTextStyle(): TextStyle {
    return MaterialTheme.typography.lTextDefault
}

@Composable
fun selectedTextColor(): Color {
    return MaterialTheme.colorScheme.primary
}

@Composable
fun deselectedTextColor(): Color {
    return MaterialTheme.colorScheme.onSurface
}
