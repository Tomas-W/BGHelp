package com.example.bghelp.ui.screens.task.add.remind

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.example.bghelp.ui.components.CustomDropdown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bghelp.constants.UiConstants as UI
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRipple
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.TimeUnit
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun ReminderInput(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int = AddTaskConstants.MIN_REMINDER,
    maxValue: Int = AddTaskConstants.MAX_REMINDER,
    isActive: Boolean = false,
    onActiveChange: (Boolean) -> Unit = {}
) = WithRipple {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var inputBuffer by remember { mutableStateOf("") }

    fun dismissKeyboard() {
        onActiveChange(false)
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    fun clampValue(value: Int): Int = value.coerceIn(minValue, maxValue)

    fun handleInput(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(3)
        inputBuffer = digits
        if (digits.isNotEmpty()) {
            val num = digits.toIntOrNull()?.let { clampValue(it) }
            if (num != null) {
                onValueChange(num)
            }
        }
    }

    BasicTextField(
        value = inputBuffer,
        onValueChange = { new ->
            if (isActive) {
                handleInput(new)
            }
        },
        modifier = Modifier
            .width(1.dp)
            .alpha(0f)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && isActive) {
                    onActiveChange(false)
                }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { dismissKeyboard() }
        ),
        singleLine = true

    )

    LaunchedEffect(isActive) {
        if (isActive) {
            focusRequester.requestFocus()
            keyboardController?.show()
            inputBuffer = ""
        }
    }

    Row(
        modifier = modifier
            .width(AddTaskConstants.REMINDER_INPUT_WIDTH.dp)
            .clip(RoundedCornerShape(Sizes.Corner.Small))
            .clickable {
                if (isActive) {
                    dismissKeyboard()
                } else {
                    onActiveChange(true)
                }
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clickableRipple(onClick = {
                    if (isActive) {
                        dismissKeyboard()
                    } else {
                        onActiveChange(true)
                    }
                }),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = if (isActive) TextStyles.Main.Bold.Small else TextStyles.Default.Small
            )
        }
    }
}

@Composable
fun TimeUnitDropdown(
    selectedUnit: TimeUnit,
    onUnitSelected: (TimeUnit) -> Unit,
    modifier: Modifier = Modifier,
    onDropdownClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    val units = remember { TimeUnit.entries }
    val unitLabels = remember {
        mapOf(
            TimeUnit.MINUTES to AddTaskStrings.MINUTES,
            TimeUnit.HOURS to AddTaskStrings.HOURS,
            TimeUnit.DAYS to AddTaskStrings.DAYS,
            TimeUnit.WEEKS to AddTaskStrings.WEEKS,
            TimeUnit.MONTHS to AddTaskStrings.MONTHS
        )
    }

    Box(modifier = modifier) {
        Text(
            text = unitLabels[selectedUnit] ?: "",
            style = TextStyles.Default.Small,
            modifier = Modifier
                .width(80.dp)
                .clickable {
                    onDropdownClick()
                    isExpanded = true
                }
        )

        CustomDropdown(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            units.forEach { unit ->
                DropdownItem(
                    label = unitLabels[unit] ?: "",
                    onClick = {
                        onUnitSelected(unit)
                        isExpanded = false
                    },
                    textStyle = TextStyles.Default.Small
                )
            }
        }
    }
}
