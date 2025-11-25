package com.example.bghelp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.theme.mTextSemi

@Composable
fun CancelButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.mTextSemi
        )
    }
}

@Composable
fun ConfirmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier,
        enabled = true,
        onClick = onClick,
        colors = if (enabled) {
            ButtonDefaults.buttonColors()
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.mTextSemi
        )
    }
}

