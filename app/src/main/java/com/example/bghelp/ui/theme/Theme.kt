package com.example.bghelp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

val ColorBlack = Color(0, 0, 0)
val ColorGrey = Color(80, 80, 80)
val ColorWhite = Color(255, 255, 255)

val MainBlue = Color(51, 102, 178)
val SecondaryBlue = Color(200, 220, 245)
val BackgroundGrey = Color(240, 240, 240)
val BackgroundDimmed = Color(233, 233, 233)
val BackgroundBright = Color(250, 250, 250)
val SecondaryGrey = Color(210, 210, 210)
val TertiaryGrey = Color(185, 185, 185)
val SnackbarGrey = Color(65, 65, 65)

val TaskDefault = Color(200, 220, 245) // Linked to domain/constants/Seeds.kt for now



val DarkTaskDefault = Color(72, 128, 210)

// Semantic colors for text
val ErrorRed = Color(220, 53, 69)
val WarningOrange = Color(255, 193, 7)
val SuccessGreen = Color(40, 167, 69)
val InfoBlue = Color(23, 162, 184)

private val LightColorScheme = lightColorScheme(
    // Main app color
    background = BackgroundGrey,
    onBackground = ColorBlack,

    // Main accents
    primary = MainBlue,
    onPrimary = ColorWhite,

    // Secondary accents
    secondary = SecondaryBlue,
    onSecondary = ColorBlack,

    // Anti highlights
    tertiary = SecondaryGrey,
    onTertiary = ColorBlack,

    // Dark grey
    surfaceVariant = TertiaryGrey,
    onSurfaceVariant = ColorGrey,

    // Cards, sheets, dialogs, navigation drawers
    surface = BackgroundGrey,
    // Black text
    onSurface = ColorBlack,

    // Main button feedback - reversed for dark mode
    surfaceDim = BackgroundDimmed,
    surfaceBright = BackgroundBright,

    // Error states (text in TextField errors, error buttons)
    error = ErrorRed,
    // Text/icons displayed on error-colored surfaces
    onError = ColorBlack,

    // Container shades for navigation bars, bottom sheets
    surfaceContainer = BackgroundGrey,
    // Highest elevation
    surfaceContainerHighest = BackgroundBright,
    // High elevation container (e.g., elevated cards)
    surfaceContainerHigh = BackgroundDimmed,
    // Low elevation container (e.g., subtle dividers)
    surfaceContainerLow = BackgroundGrey,
    // Dims
    surfaceContainerLowest = Color(0, 0, 0, (0.3*255).toInt()),

    // Borders/dividers
    outline = SecondaryGrey,
    outlineVariant = SecondaryGrey,

    // Used when UI inverts colors (pull-to-refresh, etc.)
    inversePrimary = SecondaryBlue,
    inverseSurface = SnackbarGrey,
    inverseOnSurface = ColorWhite,

    // Overlays/backdrops (modal scrims)
    scrim = TertiaryGrey
)

val DarkColorBlack = Color(0, 0, 0)
val DarkColorGrey = Color(160, 160, 160)
val DarkColorWhite = Color(200, 200, 200)

//val MainBlue = Color(51, 102, 178)
//val SecondaryBlue = Color(200, 220, 245)

val DarkMainBlue = Color(51, 102, 178, 225)
val DarkSecondaryBlue = Color(51, 102, 178, 75)
val DarkBackgroundGrey = Color(18, 18, 18)
val DarkBackgroundDimmed = Color(24, 24, 24)
val DarkBackgroundBright = Color(32, 32, 32)
val DarkSecondaryGrey = Color(60, 60, 60)
val DarkTertiaryGrey = Color(45, 45, 45)

private val DarkColorScheme = darkColorScheme(
    background = DarkBackgroundGrey,
    onBackground = DarkColorWhite,

    primary = DarkMainBlue,
    onPrimary = DarkColorBlack,

    secondary = DarkSecondaryBlue,
    onSecondary = DarkColorWhite,

    tertiary = DarkSecondaryGrey,
    onTertiary = DarkColorWhite,

    surfaceVariant = DarkTertiaryGrey,
    onSurfaceVariant = DarkColorGrey,

    surface = DarkBackgroundDimmed,
    onSurface = DarkColorWhite,

    surfaceDim = DarkColorBlack,
    surfaceBright = DarkBackgroundBright,

    error = ErrorRed,
    onError = DarkColorBlack,

    surfaceContainer = DarkBackgroundDimmed,
    surfaceContainerHighest = DarkSecondaryGrey,
    surfaceContainerHigh = Color(40, 40, 40),
    surfaceContainerLow = DarkBackgroundBright,
    surfaceContainerLowest = Color(12, 12, 12),

    outline = DarkSecondaryGrey,
    outlineVariant = DarkTertiaryGrey,

    inversePrimary = MainBlue,
    inverseSurface = DarkColorWhite,
    inverseOnSurface = DarkColorBlack,

    scrim = DarkColorBlack
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BGHelpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
