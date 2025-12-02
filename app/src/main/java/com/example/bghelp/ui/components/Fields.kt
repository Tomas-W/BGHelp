package com.example.bghelp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.bghelp.ui.theme.Sizes

@Composable
fun OutlinedStringInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    textStyle: TextStyle,
    isMultiLine: Boolean = true,
    minLines: Int,
    maxLines: Int,
    isActive: Boolean = false,
    onActiveChange: (Boolean) -> Unit = {},
    onDone: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    fun dismissKeyboard() {
        focusManager.clearFocus()
    }

    // Request focus and show keyboard when active
    LaunchedEffect(isActive) {
        if (isActive) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    val showPlaceholder = remember(value, isActive) { value.isEmpty() && !isActive }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue: String ->
                // Enforce maxLines constraint
                val lines = newValue.lines()
                if (lines.size > maxLines) {
                    val visibleText = lines.take(maxLines).joinToString("\n")
                    onValueChange(visibleText)
                } else {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && !isActive) {
                        onActiveChange(true)
                    } else if (!focusState.isFocused && isActive) {
                        onActiveChange(false)
                    }
                },
            label = {
                Text(
                    text = hint,
                    style = textStyle,
                    color = if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSecondary
                )
            },
            placeholder = if (showPlaceholder) {
                {
                    Text(
                        text = hint,
                        style = textStyle,
                        color = if (isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSecondary
                    )
                }
            } else null,
            textStyle = textStyle,
            singleLine = !isMultiLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                imeAction = if (isMultiLine) ImeAction.Default else ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    dismissKeyboard()
                    onDone()
                }
            ),
            shape = RoundedCornerShape(Sizes.Corner.S),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
