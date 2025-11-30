package com.example.bghelp.ui.screens.task.add.remind

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRipple
import com.example.bghelp.ui.components.deselectedDropdownStyle
import com.example.bghelp.ui.components.deselectedTextColor
import com.example.bghelp.ui.components.deselectedTextStyle
import com.example.bghelp.ui.components.selectedDropdownStyle
import com.example.bghelp.ui.components.selectedTextColor
import com.example.bghelp.ui.components.selectedTextStyle
import com.example.bghelp.domain.model.TimeUnit
import com.example.bghelp.ui.screens.task.add.ActiveReminderInput
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.getTimeUnitMap
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.lTextBold
import com.example.bghelp.ui.theme.lTextDefault
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST

@Composable
fun SnoozeSelection(
    viewModel: AddTaskViewModel
) {
    val snoozeValue1 by viewModel.snoozeValue1.collectAsState()
    val snoozeUnit1 by viewModel.snoozeUnit1.collectAsState()
    val snoozeValue2 by viewModel.snoozeValue2.collectAsState()
    val snoozeUnit2 by viewModel.snoozeUnit2.collectAsState()
    val activeReminderInput by viewModel.activeReminderInput.collectAsState()

    // Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(Sizes.Icon.M),
            painter = painterResource(R.drawable.snooze),
            contentDescription = stringResource(R.string.task_snooze_options)
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.M))

        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.task_snooze_options),
            style = MaterialTheme.typography.lTextBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    AddTaskSpacerSmall()

    // Snooze inputs
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SnoozeInput(
            value = snoozeValue1,
            unit = snoozeUnit1,
            snoozeIndex = 1,
            viewModel = viewModel,
            activeReminderInput = activeReminderInput
        )

        AddTaskSpacerSmall()

        SnoozeInput(
            value = snoozeValue2,
            unit = snoozeUnit2,
            snoozeIndex = 2,
            viewModel = viewModel,
            activeReminderInput = activeReminderInput
        )
    }
}

@Composable
private fun SnoozeInput(
    value: Int,
    unit: TimeUnit,
    snoozeIndex: Int,
    viewModel: AddTaskViewModel,
    activeReminderInput: ActiveReminderInput?
) {
    val isActive = activeReminderInput?.snoozeIndex == snoozeIndex

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Sizes.Size.L),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeInput(
            value = value,
            onValueChange = { newValue: Int ->
                viewModel.updateSnooze(snoozeIndex, value = newValue)
            },
            isActive = isActive,
            onActiveChange = { active: Boolean ->
                if (active) {
                    viewModel.setActiveSnoozeInput(snoozeIndex)
                } else {
                    viewModel.clearReminderInputSelection()
                }
            }
        )

        TimeDropdown(
            selectedUnit = unit,
            onUnitSelected = { newUnit: TimeUnit ->
                viewModel.updateSnooze(snoozeIndex, timeUnit = newUnit)
            },
            onDropdownClick = {
                viewModel.clearReminderInputSelection()
            }
        )
    }
}

@Composable
private fun TimeInput(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int = CONST.MIN_REMINDER,
    maxValue: Int = CONST.MAX_REMINDER,
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
            .width(CONST.REMINDER_INPUT_WIDTH.dp)
            .clip(RoundedCornerShape(Sizes.Corner.S))
            .clickable {
                if (isActive) {
                    dismissKeyboard()
                } else {
                    onActiveChange(true)
                }
            },
        horizontalArrangement = Arrangement.End,
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
                style = if (isActive) {
                    selectedTextStyle()
                } else {
                    deselectedTextStyle()
                },
                color = if (isActive) {
                    selectedTextColor()
                } else {
                    deselectedTextColor()
                }
            )
        }
    }
}

@Composable
private fun TimeDropdown(
    selectedUnit: TimeUnit,
    onUnitSelected: (TimeUnit) -> Unit,
    modifier: Modifier = Modifier,
    onDropdownClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    val units = remember { TimeUnit.entries }
    val unitLabels = getTimeUnitMap()

    Box(modifier = modifier) {
        Text(
            text = unitLabels[selectedUnit] ?: "",
            style = MaterialTheme.typography.lTextDefault,
            modifier = Modifier
                .widthIn(min = 100.dp)
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
                    textStyle = if (selectedUnit == unit) {
                        selectedDropdownStyle()
                    } else {
                        deselectedDropdownStyle()
                    },
                    spacing = Sizes.Icon.M
                )
            }
        }
    }
}

