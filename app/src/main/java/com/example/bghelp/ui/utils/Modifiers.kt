package com.example.bghelp.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.theme.SecondaryGrey

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
    strokeWidth: Dp = 1.dp,
    color: Color = SecondaryGrey
) = this.then(
    Modifier.drawWithContent {
        drawContent()

        val strokePx = strokeWidth.toPx()
        val halfStroke = strokePx / 2
        drawLine(
            color = color,
            start = Offset(0f, halfStroke),
            end = Offset(size.width, halfStroke),
            strokeWidth = strokePx
        )
    }
)

fun Modifier.bottomBorder(
    strokeWidth: Dp = 1.dp,
    color: Color = SecondaryGrey
) = this.then(
    Modifier.drawWithContent {
        drawContent()

        val strokePx = strokeWidth.toPx()
        val halfStroke = strokePx / 2
        drawLine(
            color = color,
            start = Offset(0f, size.height - halfStroke),
            end = Offset(size.width, size.height - halfStroke),
            strokeWidth = strokePx
        )
    }
)