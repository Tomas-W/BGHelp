package com.example.bghelp.domain.model

import androidx.compose.ui.graphics.Color

data class FeatureColor(
    val id: Int,
    val name: String,
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Float
) {
    fun toComposeColor(): Color = Color(
        red = red / 255f,
        green = green / 255f,
        blue = blue / 255f,
        alpha = alpha
    )
}

data class CreateFeatureColor(
    val name: String,
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Float,
    val isDefault: Boolean = false
)


