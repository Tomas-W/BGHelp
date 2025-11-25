package com.example.bghelp.ui.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Dark theme used when system/app theme requests it (currently we always force light)
private val DarkColorScheme = darkColorScheme(
    primary = Color.Unspecified,
    onPrimary = Color.Unspecified, // TODO: Text/icons on primary surfaces
    secondary = Color.Unspecified,
    onSecondary = Color.Unspecified, // TODO: Text/icons on secondary surfaces
    tertiary = Color.Unspecified,
    onTertiary = Color.Unspecified, // TODO: Text/icons on tertiary surfaces
    error = Color.Unspecified, // TODO: Error states
    onError = Color.Unspecified, // TODO: Text/icons on error surfaces
    background = Color.Unspecified,
    onBackground = Color.Unspecified, // TODO: Default text color
    surface = Color.Unspecified,
    onSurface = Color.Unspecified, // TODO: Text/icons on surfaces
    surfaceVariant = Color.Unspecified, // TODO: Alternative surface for cards/chips
    onSurfaceVariant = Color.Unspecified, // TODO: Text/icons on surfaceVariant
    surfaceContainer = Color.Unspecified, // TODO: Container shades
    surfaceContainerHighest = Color.Unspecified, // TODO: Highest elevation container
    surfaceContainerHigh = Color.Unspecified, // TODO: High elevation container
    surfaceContainerLow = Color.Unspecified, // TODO: Low elevation container
    surfaceContainerLowest = Color.Unspecified, // TODO: Lowest elevation container
    surfaceDim = Color.Unspecified, // TODO: Dimmed surface variant
    surfaceBright = Color.Unspecified, // TODO: Bright surface variant
    outline = Color.Unspecified, // TODO: Borders/dividers
    outlineVariant = Color.Unspecified, // TODO: Lighter outline variant
    inversePrimary = Color.Unspecified, // TODO: Primary when UI inverts
    inverseSurface = Color.Unspecified, // TODO: Surface when UI inverts
    inverseOnSurface = Color.Unspecified, // TODO: Text/icons on inverseSurface
    scrim = Color.Unspecified // TODO: Overlays/backdrops
)

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
    surfaceVariant = Color.Red, // TODO: Set alternative surface color
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
    surfaceContainerHighest = Color.Red,
    // High elevation container (e.g., elevated cards)
    surfaceContainerHigh = Color.Red, // TODO: Set high elevation container
    // Low elevation container (e.g., subtle dividers)
    surfaceContainerLow = Color.Red, // TODO: Set low elevation container
    // Dims
    surfaceContainerLowest = Color(0, 0, 0, (0.3*255).toInt()),

    // Borders/dividers
    outline = SecondaryGrey,
    outlineVariant = SecondaryGrey,

    // Used when UI inverts colors (pull-to-refresh, etc.)
    inversePrimary = Color.Red, // TODO: Set inverse primary color
    // Snackbar
    inverseSurface = SnackbarGrey, // TODO: Set inverse surface color
    // Snackbar text
    inverseOnSurface = Color.White, // TODO: Set text color for inverseSurface

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
