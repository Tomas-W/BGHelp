package com.example.bghelp.ui.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// Dark theme used when system/app theme requests it (currently we always force light)
private val DarkColorScheme = darkColorScheme(
    primary = MainBlue,
    secondary = SecondaryBlue,
    tertiary = SecondaryGrey,
    background = BackgroundGrey,
    surface = BackgroundGrey
)

// Light theme – base palette for every composable unless you override colors locally.
// Buttons/FABs/active icons consume primary, cards/sheets use surface, and text components
// pick the matching on* color (onPrimary/onSurface/onBackground) through MaterialTheme.
private val LightColorScheme = lightColorScheme(
    // Buttons, FABs, active icons, focused indicators
    primary = MainBlue,
    // Text/icons displayed on primary surfaces (e.g., FAB icon)
    onPrimary = TextWhite,

    // Secondary accents: chips, toggles, secondary buttons
    secondary = SecondaryBlue,
    onSecondary = TextBlack,

    // Tertiary accents: subtle dividers, secondary icons/text
    tertiary = SecondaryGrey,
    onTertiary = TextGrey,

    // Error states (text in TextField errors, error buttons)
    error = ErrorRed,

    // Screen backgrounds and default text color
    background = BackgroundGrey,
    onBackground = TextBlack,

    // Cards, sheets, dialogs, navigation drawers
    surface = BackgroundGrey,
    onSurface = TextBlack,

    // Container shades for navigation bars, bottom sheets
    surfaceContainer = BackgroundGrey,

    // Borders/dividers
    outline = SecondaryGrey,

    // Used when UI inverts colors (pull-to-refresh, etc.)
    inversePrimary = TextWhite,

    // Overlays/backdrops (modal scrims)
    scrim = TertiaryGrey
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
