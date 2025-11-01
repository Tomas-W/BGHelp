package com.example.bghelp.ui.screens.task.add.title

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun TitleInput(
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
        onActiveChange(false)
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    LaunchedEffect(isActive) {
        if (isActive) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    val hasText = value.isNotEmpty()
    val shouldFloatLabel = isActive || hasText

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    // Outline
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.tertiary
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                // Counter internal touch padding
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                )
        ) {
            // Input
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
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
                textStyle = textStyle,
                keyboardOptions = KeyboardOptions(
                    imeAction = if (isMultiLine) ImeAction.Default else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        dismissKeyboard()
                        onDone()
                    }
                ),
                singleLine = !isMultiLine,
                maxLines = maxLines,
                minLines = minLines,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty() && !isActive) {
                            Text(
                                text = hint,
                                style = TextStyles.Grey.Small
                            )
                        }
                        innerTextField()
                    }
                },
                onTextLayout = { textLayoutResult ->
                    // Prevent typing beyond maxLines
                    if (textLayoutResult.lineCount > maxLines) {
                        val visibleText = value.lines().take(maxLines).joinToString("\n")
                        if (value != visibleText) {
                            onValueChange(visibleText)
                        }
                    }
                }
            )
        }

        // Label in outline
        if (shouldFloatLabel) {
            Text(
                text = hint,
                style = TextStyles.Default.Small.copy(
                    color = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    background = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .offset(x = 12.dp, y = (-8).dp)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}
