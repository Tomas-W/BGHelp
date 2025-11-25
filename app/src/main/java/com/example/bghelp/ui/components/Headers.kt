package com.example.bghelp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.bghelp.ui.theme.lHeaderBold

@Composable
fun MainHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.lHeaderBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
