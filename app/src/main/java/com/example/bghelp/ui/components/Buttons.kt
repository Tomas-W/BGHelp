package com.example.bghelp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.theme.mTextSemi
import com.example.bghelp.ui.utils.topBorder

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

@Composable
fun ButtonRow(
    modifier: Modifier = Modifier,
    cancelText: String,
    confirmText: String,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    isValid: Boolean,
    isLoading: Boolean,

    middleText: String? = null,
    onMiddleClick: (() -> Unit)? = null
) {
    val cancelInteractionSource = remember { MutableInteractionSource() }
    val cancelIsPressed by cancelInteractionSource.collectIsPressedAsState()

    val middleInteractionSource = remember { MutableInteractionSource() }
    val middleIsPressed by middleInteractionSource.collectIsPressedAsState()

    val confirmInteractionSource = remember { MutableInteractionSource() }
    val confirmIsPressed by confirmInteractionSource.collectIsPressedAsState()

    val defaultColor = MaterialTheme.colorScheme.surface
    val highlightColor = MaterialTheme.colorScheme.surfaceDim
    val hasMiddleButton = middleText != null && onMiddleClick != null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(if (cancelIsPressed) highlightColor else defaultColor)
                .topBorder()
                .clickable(
                    interactionSource = cancelInteractionSource,
                    indication = null,
                    enabled = !isLoading
                ) {
                    if (!isLoading) {
                        onCancelClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = cancelText,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        if (hasMiddleButton) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (middleIsPressed) highlightColor else defaultColor)
                    .topBorder()
                    .clickable(
                        interactionSource = middleInteractionSource,
                        indication = null,
                        enabled = !isLoading
                    ) {
                        if (!isLoading) {
                            onMiddleClick.invoke()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = middleText,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .background(if (confirmIsPressed) highlightColor else defaultColor)
                .topBorder()
                .clickable(
                    interactionSource = confirmInteractionSource,
                    indication = null,
                    enabled = !isLoading
                ) {
                    if (!isLoading) {
                        onConfirmClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isValid && !isLoading) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }
                )
            }
        }
    }
}
