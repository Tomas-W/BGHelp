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
    isValid: Boolean,
    isLoading: Boolean,
    firstLabel: String,
    firstOnClick: () -> Unit,
    secondLabel: String? = null,
    secondOnClick: (() -> Unit)? = null,
    thirdLabel: String? = null,
    thirdOnClick: (() -> Unit)? = null
) {
    val defaultColor = MaterialTheme.colorScheme.surface
    val highlightColor = MaterialTheme.colorScheme.surfaceDim
    val hasSecond = secondLabel != null && secondOnClick != null
    val hasThird = thirdLabel != null && thirdOnClick != null

    val firstInteractionSource = remember { MutableInteractionSource() }
    val firstIsPressed by firstInteractionSource.collectIsPressedAsState()

    val secondInteractionSource = remember { MutableInteractionSource() }
    val secondIsPressed by secondInteractionSource.collectIsPressedAsState()

    val thirdInteractionSource = remember { MutableInteractionSource() }
    val thirdIsPressed by thirdInteractionSource.collectIsPressedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasSecond) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (secondIsPressed) highlightColor else defaultColor)
                    .topBorder()
                    .clickable(
                        interactionSource = secondInteractionSource,
                        indication = null,
                        enabled = !isLoading
                    ) {
                        if (!isLoading) {
                            secondOnClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = secondLabel,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        if (hasThird) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (thirdIsPressed) highlightColor else defaultColor)
                    .topBorder()
                    .clickable(
                        interactionSource = thirdInteractionSource,
                        indication = null,
                        enabled = !isLoading
                    ) {
                        if (!isLoading) {
                            thirdOnClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = thirdLabel,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .background(if (firstIsPressed) highlightColor else defaultColor)
                .topBorder()
                .clickable(
                    interactionSource = firstInteractionSource,
                    indication = null,
                    enabled = !isLoading
                ) {
                    if (!isLoading) {
                        firstOnClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = firstLabel,
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
