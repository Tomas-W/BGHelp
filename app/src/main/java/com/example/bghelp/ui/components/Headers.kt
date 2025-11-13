package com.example.bghelp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bghelp.ui.theme.TextSizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun MainHeader(text: String) {
    Text(
        text = text,
        style = TextStyles.Grey.Bold.L
    )
}
