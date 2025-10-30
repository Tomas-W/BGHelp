package com.example.bghelp.ui.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithRipple(content: @Composable () -> Unit) {
    val rippleConfig = RippleConfiguration()
    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfig) {
        content()
    }
}

@Composable
fun Modifier.clickableWithUnboundedRipple(
    onClick: () -> Unit,
    enabled: Boolean = true,
    radius: Dp = 22.dp
): Modifier {
    val indication = ripple(
        bounded = false,
        radius = radius,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )
    val interactionSource = remember { MutableInteractionSource() }
    
    return this.clickable(
        onClick = onClick,
        enabled = enabled,
        indication = indication,
        interactionSource = interactionSource
    )
}

@Composable
fun Modifier.clickableWithCalendarRipple(
    onClick: () -> Unit,
    enabled: Boolean = true
): Modifier {
    val indication = ripple(
        bounded = true,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    )
    val interactionSource = remember { MutableInteractionSource() }
    
    return this.clickable(
        onClick = onClick,
        enabled = enabled,
        indication = indication,
        interactionSource = interactionSource
    )
}
