package com.example.bghelp.ui.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = MainBlue,
    secondary = SecondaryBlue,
    tertiary = SecondaryGrey,
    background = BackgroundGrey,
    surface = BackgroundGrey
)

private val LightColorScheme = lightColorScheme(
    primary = MainBlue, // Main brand color for buttons, FABs, active states
    onPrimary = TextWhite, // Text/icons on primary colored elements
    // primaryContainer = MainBlue.copy(alpha = 0.1f), // Subtle primary backgrounds
    // onPrimaryContainer = MainBlue, // Text on primary container backgrounds
    secondary = SecondaryBlue, // Secondary brand color for accent elements
    onSecondary = TextBlack, // Text/icons on secondary colored elements
//    secondaryContainer = ButtonGrey, // Subtle secondary backgrounds
    // onSecondaryContainer = SecondaryBlue, // Text on secondary container backgrounds
    tertiary = SecondaryGrey, // Third brand color for additional accents
    onTertiary = TextGrey, // Text/icons on tertiary colored elements
    // tertiaryContainer = SecondaryGrey.copy(alpha = 0.1f), // Subtle tertiary backgrounds
    // onTertiaryContainer = SecondaryGrey, // Text on tertiary container backgrounds
    error = ErrorRed, // Error state color
    // onError = Color.White, // Text/icons on error colored elements
    // errorContainer = Color(220, 53, 69).copy(alpha = 0.1f), // Subtle error backgrounds
    // onErrorContainer = Color(220, 53, 69), // Text on error container backgrounds
    background = BackgroundGrey, // Main app background color
    onBackground = TextBlack, // Default text color on background
    surface = BackgroundGrey, // Card, sheet, and dialog background color
    onSurface = TextBlack, // Text on cards, sheets, dialogs, disabled elements
    // surfaceVariant = BackgroundGrey.copy(alpha = 0.8f), // Alternative surface backgrounds
    // onSurfaceVariant = Color(65, 65, 65), // Text on surface variant backgrounds
    surfaceContainer = BackgroundGrey, // Navigation bar and container background color
    // surfaceContainerHigh = BottomNavColor.copy(alpha = 0.9f), // High elevation containers
    // surfaceContainerHighest = BottomNavColor.copy(alpha = 0.8f), // Highest elevation containers
    outline = SecondaryGrey, // Border/outline color
    // outlineVariant = Color(210, 210, 210).copy(alpha = 0.5f), // Alternative outline
    // inverseSurface = Color.Black, // Inverse surface backgrounds
    // inverseOnSurface = Color.White, // Text on inverse surface backgrounds
     inversePrimary = TextWhite, // Primary color in inverse contexts
     scrim = TertiaryGrey // Overlay/backdrop color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BGHelpTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
