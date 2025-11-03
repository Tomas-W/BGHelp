package com.example.bghelp.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * Modifier that dismisses keyboard and clears focus when tapping outside inputs.
 * 
 * @param onAdditionalDismiss Optional callback for additional cleanup (e.g., clearing custom input states)
 */
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

/**
 * Clickable modifier that clears focus before executing onClick.
 * Use this for buttons, headers, and other interactive elements that should dismiss the keyboard.
 * 
 * @param onClick The action to perform after clearing focus
 */
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

