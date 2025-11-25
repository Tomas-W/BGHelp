package com.example.bghelp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaultFamily = FontFamily.Default

// EXTRA SMALL TEXT
val Typography.xsTextDefault: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    )
val Typography.xsTextSemi: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    )
val Typography.xsTextBold: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    )

// SMALL TEXT
val Typography.sTextDefault: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    )
val Typography.sTextSemi: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    )
val Typography.sTextBold: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    )
val Typography.sTextItalic: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp,
        fontStyle = FontStyle.Italic
    )

// MEDIUM TEXT
val Typography.mTextDefault: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
val Typography.mTextSemi: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
val Typography.mTextBold: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    )
val Typography.mTextItalic: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        fontStyle = FontStyle.Italic
    )

// LARGE TEXT
val Typography.lTextDefault: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    )
val Typography.lTextSemi: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.1.sp
    )
val Typography.lTextBold: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.2.sp
    )
val Typography.lTextItalic: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 1.sp,
        fontStyle = FontStyle.Italic
    )

// SMALL HEADER
val Typography.sHeaderBold: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )
val Typography.sHeaderSemi: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    )

// MEDIUM HEADER
val Typography.mHeaderBold: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )
val Typography.mHeaderSemi: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )

// LARGE HEADER
val Typography.lHeaderBold: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
val Typography.lHeaderSemi: TextStyle
    get() = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )





val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = defaultFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
