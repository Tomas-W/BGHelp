package com.example.bghelp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object TextSizes {
    const val EXTRA_SMALL = 16f
    const val SMALL = 18f
    const val MEDIUM = 20f
    const val LARGE = 26f
    const val EXTRA_LARGE = 32f
}

object TextStyles {
    private fun create(
        size: Float,
        weight: FontWeight = FontWeight.Normal,
        color: Color = TextBlack,
        style: FontStyle = FontStyle.Normal
    ) = TextStyle(
        fontSize = size.sp,
        fontWeight = weight,
        color = color,
        fontStyle = style
    )

    // Default (Black)
    object Default {
        val ExtraSmall = create(TextSizes.EXTRA_SMALL)
        val Small = create(TextSizes.SMALL)
        val Medium = create(TextSizes.MEDIUM)
        val Large = create(TextSizes.LARGE)
        val ExtraLarge = create(TextSizes.EXTRA_LARGE)

        object Bold {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, weight = FontWeight.Bold)
            val Small = create(TextSizes.SMALL, weight = FontWeight.Bold)
            val Medium = create(TextSizes.MEDIUM, weight = FontWeight.Bold)
            val Large = create(TextSizes.LARGE, weight = FontWeight.Bold)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, weight = FontWeight.Bold)
        }

        object Italic {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, style = FontStyle.Italic)
            val Small = create(TextSizes.SMALL, style = FontStyle.Italic)
            val Medium = create(TextSizes.MEDIUM, style = FontStyle.Italic)
            val Large = create(TextSizes.LARGE, style = FontStyle.Italic)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, style = FontStyle.Italic)
        }
    }

    // Grey
    object Grey {
        val ExtraSmall = create(TextSizes.EXTRA_SMALL, color = TextGrey)
        val Small = create(TextSizes.SMALL, color = TextGrey)
        val Medium = create(TextSizes.MEDIUM, color = TextGrey)
        val Large = create(TextSizes.LARGE, color = TextGrey)
        val ExtraLarge = create(TextSizes.EXTRA_LARGE, color = TextGrey)

        object Bold {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, weight = FontWeight.Bold, color = TextGrey)
            val Small = create(TextSizes.SMALL, weight = FontWeight.Bold, color = TextGrey)
            val Medium = create(TextSizes.MEDIUM, weight = FontWeight.Bold, color = TextGrey)
            val Large = create(TextSizes.LARGE, weight = FontWeight.Bold, color = TextGrey)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, weight = FontWeight.Bold, color = TextGrey)
        }

        object Italic {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, style = FontStyle.Italic, color = TextGrey)
            val Small = create(TextSizes.SMALL, style = FontStyle.Italic, color = TextGrey)
            val Medium = create(TextSizes.MEDIUM, style = FontStyle.Italic, color = TextGrey)
            val Large = create(TextSizes.LARGE, style = FontStyle.Italic, color = TextGrey)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, style = FontStyle.Italic, color = TextGrey)
        }
    }

    // White
    object White {
        val ExtraSmall = create(TextSizes.EXTRA_SMALL, color = TextWhite)
        val Small = create(TextSizes.SMALL, color = TextWhite)
        val Medium = create(TextSizes.MEDIUM, color = TextWhite)
        val Large = create(TextSizes.LARGE, color = TextWhite)
        val ExtraLarge = create(TextSizes.EXTRA_LARGE, color = TextWhite)

        object Bold {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, weight = FontWeight.Bold, color = TextWhite)
            val Small = create(TextSizes.SMALL, weight = FontWeight.Bold, color = TextWhite)
            val Medium = create(TextSizes.MEDIUM, weight = FontWeight.Bold, color = TextWhite)
            val Large = create(TextSizes.LARGE, weight = FontWeight.Bold, color = TextWhite)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, weight = FontWeight.Bold, color = TextWhite)
        }

        object Italic {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, style = FontStyle.Italic, color = TextWhite)
            val Small = create(TextSizes.SMALL, style = FontStyle.Italic, color = TextWhite)
            val Medium = create(TextSizes.MEDIUM, style = FontStyle.Italic, color = TextWhite)
            val Large = create(TextSizes.LARGE, style = FontStyle.Italic, color = TextWhite)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, style = FontStyle.Italic, color = TextWhite)
        }
    }

    // Error (Red)
    object Error {
        val ExtraSmall = create(TextSizes.EXTRA_SMALL, color = ErrorRed)
        val Small = create(TextSizes.SMALL, color = ErrorRed)
        val Medium = create(TextSizes.MEDIUM, color = ErrorRed)
        val Large = create(TextSizes.LARGE, color = ErrorRed)
        val ExtraLarge = create(TextSizes.EXTRA_LARGE, color = ErrorRed)

        object Bold {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, weight = FontWeight.Bold, color = ErrorRed)
            val Small = create(TextSizes.SMALL, weight = FontWeight.Bold, color = ErrorRed)
            val Medium = create(TextSizes.MEDIUM, weight = FontWeight.Bold, color = ErrorRed)
            val Large = create(TextSizes.LARGE, weight = FontWeight.Bold, color = ErrorRed)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, weight = FontWeight.Bold, color = ErrorRed)
        }

        object Italic {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, style = FontStyle.Italic, color = ErrorRed)
            val Small = create(TextSizes.SMALL, style = FontStyle.Italic, color = ErrorRed)
            val Medium = create(TextSizes.MEDIUM, style = FontStyle.Italic, color = ErrorRed)
            val Large = create(TextSizes.LARGE, style = FontStyle.Italic, color = ErrorRed)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, style = FontStyle.Italic, color = ErrorRed)
        }
    }

    // Warning (Orange)
    object Warning {
        val ExtraSmall = create(TextSizes.EXTRA_SMALL, color = WarningOrange)
        val Small = create(TextSizes.SMALL, color = WarningOrange)
        val Medium = create(TextSizes.MEDIUM, color = WarningOrange)
        val Large = create(TextSizes.LARGE, color = WarningOrange)
        val ExtraLarge = create(TextSizes.EXTRA_LARGE, color = WarningOrange)

        object Bold {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, weight = FontWeight.Bold, color = WarningOrange)
            val Small = create(TextSizes.SMALL, weight = FontWeight.Bold, color = WarningOrange)
            val Medium = create(TextSizes.MEDIUM, weight = FontWeight.Bold, color = WarningOrange)
            val Large = create(TextSizes.LARGE, weight = FontWeight.Bold, color = WarningOrange)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, weight = FontWeight.Bold, color = WarningOrange)
        }

        object Italic {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, style = FontStyle.Italic, color = WarningOrange)
            val Small = create(TextSizes.SMALL, style = FontStyle.Italic, color = WarningOrange)
            val Medium = create(TextSizes.MEDIUM, style = FontStyle.Italic, color = WarningOrange)
            val Large = create(TextSizes.LARGE, style = FontStyle.Italic, color = WarningOrange)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, style = FontStyle.Italic, color = WarningOrange)
        }
    }

    // Success (Green)
    object Success {
        val ExtraSmall = create(TextSizes.EXTRA_SMALL, color = SuccessGreen)
        val Small = create(TextSizes.SMALL, color = SuccessGreen)
        val Medium = create(TextSizes.MEDIUM, color = SuccessGreen)
        val Large = create(TextSizes.LARGE, color = SuccessGreen)
        val ExtraLarge = create(TextSizes.EXTRA_LARGE, color = SuccessGreen)

        object Bold {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, weight = FontWeight.Bold, color = SuccessGreen)
            val Small = create(TextSizes.SMALL, weight = FontWeight.Bold, color = SuccessGreen)
            val Medium = create(TextSizes.MEDIUM, weight = FontWeight.Bold, color = SuccessGreen)
            val Large = create(TextSizes.LARGE, weight = FontWeight.Bold, color = SuccessGreen)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, weight = FontWeight.Bold, color = SuccessGreen)
        }

        object Italic {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, style = FontStyle.Italic, color = SuccessGreen)
            val Small = create(TextSizes.SMALL, style = FontStyle.Italic, color = SuccessGreen)
            val Medium = create(TextSizes.MEDIUM, style = FontStyle.Italic, color = SuccessGreen)
            val Large = create(TextSizes.LARGE, style = FontStyle.Italic, color = SuccessGreen)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, style = FontStyle.Italic, color = SuccessGreen)
        }
    }

    // Info (Blue)
    object Info {
        val ExtraSmall = create(TextSizes.EXTRA_SMALL, color = InfoBlue)
        val Small = create(TextSizes.SMALL, color = InfoBlue)
        val Medium = create(TextSizes.MEDIUM, color = InfoBlue)
        val Large = create(TextSizes.LARGE, color = InfoBlue)
        val ExtraLarge = create(TextSizes.EXTRA_LARGE, color = InfoBlue)

        object Bold {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, weight = FontWeight.Bold, color = InfoBlue)
            val Small = create(TextSizes.SMALL, weight = FontWeight.Bold, color = InfoBlue)
            val Medium = create(TextSizes.MEDIUM, weight = FontWeight.Bold, color = InfoBlue)
            val Large = create(TextSizes.LARGE, weight = FontWeight.Bold, color = InfoBlue)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, weight = FontWeight.Bold, color = InfoBlue)
        }

        object Italic {
            val ExtraSmall = create(TextSizes.EXTRA_SMALL, style = FontStyle.Italic, color = InfoBlue)
            val Small = create(TextSizes.SMALL, style = FontStyle.Italic, color = InfoBlue)
            val Medium = create(TextSizes.MEDIUM, style = FontStyle.Italic, color = InfoBlue)
            val Large = create(TextSizes.LARGE, style = FontStyle.Italic, color = InfoBlue)
            val ExtraLarge = create(TextSizes.EXTRA_LARGE, style = FontStyle.Italic, color = InfoBlue)
        }
    }

    // Custom
    fun custom(
        size: Float,
        weight: FontWeight = FontWeight.Normal,
        color: Color = TextBlack,
        style: FontStyle = FontStyle.Normal
    ) = create(size, weight, color, style)
}
