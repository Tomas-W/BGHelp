package com.example.bghelp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    // onSecondary = Color.Black, // Text/icons on secondary colored elements
    // secondaryContainer = SecondaryBlue.copy(alpha = 0.1f), // Subtle secondary backgrounds
    // onSecondaryContainer = SecondaryBlue, // Text on secondary container backgrounds
    tertiary = SecondaryGrey, // Third brand color for additional accents
    // onTertiary = Color.Black, // Text/icons on tertiary colored elements
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
    surfaceContainer = BottomNavColor, // Navigation bar and container background color
    // surfaceContainerHigh = BottomNavColor.copy(alpha = 0.9f), // High elevation containers
    // surfaceContainerHighest = BottomNavColor.copy(alpha = 0.8f), // Highest elevation containers
    outline = SecondaryGrey, // Border/outline color
    // outlineVariant = Color(210, 210, 210).copy(alpha = 0.5f), // Alternative outline
    // inverseSurface = Color.Black, // Inverse surface backgrounds
    // inverseOnSurface = Color.White, // Text on inverse surface backgrounds
    // inversePrimary = MainBlue.copy(alpha = 0.8f), // Primary color in inverse contexts
    // scrim = Color.Black.copy(alpha = 0.5f) // Overlay/backdrop color

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun BGHelpTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}