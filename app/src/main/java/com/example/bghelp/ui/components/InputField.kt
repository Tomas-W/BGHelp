package com.example.bghelp.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun OldInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    isMultiLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 4,
    onDone: () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused },
        label = { Text(text = hint, style = TextStyles.Default.Small) },
        placeholder = if (!isFocused) {
            { Text(text = hint, style = TextStyles.Grey.Small) }
        } else null,
        textStyle = TextStyles.Default.Small,
        singleLine = !isMultiLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(
            imeAction = if (isMultiLine) ImeAction.Default else ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        )
    )
}

