package com.example.bghelp.domain.model

import androidx.compose.ui.graphics.Color

data class FeatureColor(
    val id: Int,
    val name: String,
    // Task color Light
    val lightRed: Int,
    val lightGreen: Int,
    val lightBlue: Int,
    val lightAlpha: Float,
    // Task color dark
    val darkRed: Int,
    val darkGreen: Int,
    val darkBlue: Int,
    val darkAlpha: Float,
    // Text color light
    val lightTextRed: Int = 0,
    val lightTextGreen: Int = 0,
    val lightTextBlue: Int = 0,
    val lightTextAlpha: Float = 1.0f,
    // Text color dark
    val darkTextRed: Int = 0,
    val darkTextGreen: Int = 0,
    val darkTextBlue: Int = 0,
    val darkTextAlpha: Float = 1.0f
) {
    fun toComposeColor(isDarkMode: Boolean = false): Color = backgroundColor(isDarkMode = isDarkMode)

    fun backgroundColor(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color(
                red = darkRed / 255f,
                green = darkGreen / 255f,
                blue = darkBlue / 255f,
                alpha = darkAlpha
            )
        } else {
            Color(
                red = lightRed / 255f,
                green = lightGreen / 255f,
                blue = lightBlue / 255f,
                alpha = lightAlpha
            )
        }
    }

    fun textColor(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color(
                red = darkTextRed / 255f,
                green = darkTextGreen / 255f,
                blue = darkTextBlue / 255f,
                alpha = darkTextAlpha
            )
        } else {
            Color(
                red = lightTextRed / 255f,
                green = lightTextGreen / 255f,
                blue = lightTextBlue / 255f,
                alpha = lightTextAlpha
            )
        }
    }
}

data class CreateFeatureColor(
    val name: String,
    val isDefault: Boolean,
    // Task color Light
    val lightRed: Int,
    val lightGreen: Int,
    val lightBlue: Int,
    val lightAlpha: Float,
    // Task color dark
    val darkRed: Int,
    val darkGreen: Int,
    val darkBlue: Int,
    val darkAlpha: Float,
    // Text color light
    val lightTextRed: Int,
    val lightTextGreen: Int,
    val lightTextBlue: Int,
    val lightTextAlpha: Float,
    // Text color dark
    val darkTextRed: Int,
    val darkTextGreen: Int,
    val darkTextBlue: Int,
    val darkTextAlpha: Float
)

