package com.example.bghelp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.EnumMap

object TextSizes {
    const val XXS = 14f
    const val XS = 16f
    const val S = 18f
    const val M = 20f
    const val L = 24f
    const val XL = 28f
    const val XXL = 32f
}

private enum class TextSizeToken(val value: Float) {
    XXS(TextSizes.XXS),
    XS(TextSizes.XS),
    S(TextSizes.S),
    M(TextSizes.M),
    L(TextSizes.L),
    XL(TextSizes.XL),
    XXL(TextSizes.XXL)
}

private fun create(
    size: Float,
    color: Color,
    weight: FontWeight = FontWeight.Normal,
    style: FontStyle = FontStyle.Normal
) = TextStyle(
    fontSize = size.sp,
    fontWeight = weight,
    color = color,
    fontStyle = style
)

private fun create(
    size: TextSizeToken,
    color: Color,
    weight: FontWeight = FontWeight.Normal,
    style: FontStyle = FontStyle.Normal
): TextStyle = create(size.value, color, weight, style)

private class TextStyleBranch(
    private val color: Color,
    private val weight: FontWeight = FontWeight.Normal,
    private val style: FontStyle = FontStyle.Normal
) {
    private val cache = EnumMap<TextSizeToken, TextStyle>(TextSizeToken::class.java)

    private fun resolve(size: TextSizeToken): TextStyle =
        cache.getOrPut(size) { create(size, color, weight, style) }

    val XXS get() = resolve(TextSizeToken.XXS)
    val XS get() = resolve(TextSizeToken.XS)
    val S get() = resolve(TextSizeToken.S)
    val M get() = resolve(TextSizeToken.M)
    val L get() = resolve(TextSizeToken.L)
    val XL get() = resolve(TextSizeToken.XL)
    val XXL get() = resolve(TextSizeToken.XXL)
}

object TextStyles {
    private fun colorGroup(color: Color): ColorGroup = ColorGroup(color)

    val Default = colorGroup(TextBlack)
    val Grey = colorGroup(TextGrey)
    val White = colorGroup(TextWhite)
    val Error = colorGroup(ErrorRed)
    val Warning = colorGroup(WarningOrange)
    val Success = colorGroup(SuccessGreen)
    val Main = colorGroup(MainBlue)

    class ColorGroup(private val color: Color) {
        private val normal = TextStyleBranch(color)

        val XXS get() = normal.XXS
        val XS get() = normal.XS
        val S get() = normal.S
        val M get() = normal.M
        val L get() = normal.L
        val XL get() = normal.XL
        val XXL get() = normal.XXL

        val Bold = BoldGroup(color)
        val Italic = ItalicGroup(color)

        class BoldGroup internal constructor(color: Color) {
            private val branch = TextStyleBranch(color, weight = FontWeight.Bold)

            val XS get() = branch.XS
            val S get() = branch.S
            val M get() = branch.M
            val L get() = branch.L
            val XL get() = branch.XL
            val XXL get() = branch.XXL
        }

        class ItalicGroup internal constructor(color: Color) {
            private val branch = TextStyleBranch(color, style = FontStyle.Italic)

            val XXS get() = branch.XXS
            val XS get() = branch.XS
            val S get() = branch.S
            val M get() = branch.M
            val L get() = branch.L
            val XL get() = branch.XL
            val XXL get() = branch.XXL
        }
    }

    fun custom(
        size: Float,
        weight: FontWeight = FontWeight.Normal,
        color: Color = TextBlack,
        style: FontStyle = FontStyle.Normal
    ): TextStyle = create(size, color, weight, style)
}
