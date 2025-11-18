package com.example.bghelp.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp

@Composable
fun Modifier.dismissKeyboardOnTap(
    onAdditionalDismiss: () -> Unit = {}
): Modifier {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    return this.pointerInput(Unit) {
        detectTapGestures { _ ->
            focusManager.clearFocus()
            keyboardController?.hide()
            onAdditionalDismiss()
        }
    }
}

fun Modifier.clickableDismissFocus(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    
    this.clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = null
    ) {
        focusManager.clearFocus()
        onClick()
    }
}

fun Modifier.topBorder(
    strokeWidth: Dp,
    color: Color
) = this.then(
    Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidth.toPx()
        )
    }
)

fun Modifier.bottomBorder(
    strokeWidth: Dp,
    color: Color
) = this.then(
    Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidth.toPx()
        )
    }
)