package com.example.bghelp.ui.utils

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

