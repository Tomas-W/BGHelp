package com.example.bghelp.ui.screens.task.add.remind

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRipple
import com.example.bghelp.ui.components.clickableRippleDismiss
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR
import com.example.bghelp.ui.screens.task.add.Reminder
import com.example.bghelp.ui.screens.task.add.RemindType
import com.example.bghelp.ui.screens.task.add.UserDateSelection
import com.example.bghelp.ui.screens.task.add.deselectedStyle
import com.example.bghelp.ui.screens.task.add.TimeUnit
import com.example.bghelp.ui.screens.task.add.highlightedStyle
import com.example.bghelp.ui.theme.Sizes

@Composable
fun BeforeSelection (
    viewModel: AddTaskViewModel,
) {
    val startReminders by viewModel.startReminders.collectAsState()
    val endReminders by viewModel.endReminders.collectAsState()
    val isEndTimeVisible by viewModel.isEndTimeVisible.collectAsState()
    val userDateSelection by viewModel.userDateSelection.collectAsState()
    val showEndReminders = isEndTimeVisible && userDateSelection != UserDateSelection.ON

    // Before Start
    AddReminder(
        remindType = RemindType.START,
        onClick = { viewModel.addReminder(RemindType.START) },
        viewModel = viewModel
    )
    AddTaskSpacerSmall()

    // Before start items
    startReminders.forEach { reminder ->
        ReminderItem(
            reminder = reminder,
            remindType = RemindType.START,
            viewModel = viewModel
        )
    }
    if (startReminders.isNotEmpty()) AddTaskSpacerMedium()

    // Before end - only show if end time is visible and not all-day
    if (showEndReminders) {
        AddReminder(
            remindType = RemindType.END,
            onClick = { viewModel.addReminder(RemindType.END) },
            viewModel = viewModel
        )
        AddTaskSpacerSmall()

        // Before end items
        endReminders.forEach { reminder ->
            ReminderItem(
                reminder = reminder,
                remindType = RemindType.END,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun AddReminder(
    remindType: RemindType,
    onClick: () -> Unit,
    viewModel: AddTaskViewModel
) {
    val remindTypeChoices = remember { mapOf(
        RemindType.START to STR.BEFORE_START,
        RemindType.END to STR.BEFORE_END) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Before / after start
        Text(
            modifier = Modifier
                .widthIn(min = CONST.MIN_WIDTH.dp),
            text = remindTypeChoices[remindType]!!,
            style = deselectedStyle,
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.M))

        // + Icon
        WithRipple {
            Box(
                modifier = Modifier
                    .clickableRippleDismiss(
                        onClick = {
                            viewModel.clearReminderInputSelection()
                            onClick()
                        },
                        radius = Sizes.Icon.S
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(Sizes.Icon.S),
                    painter = painterResource(R.drawable.add),
                    contentDescription = STR.ADD_REMINDER
                )
            }
        }
    }
}

@Composable
private fun ReminderItem(
    reminder: Reminder,
    remindType: RemindType,
    viewModel: AddTaskViewModel
) {
    val activeReminderInput by viewModel.activeReminderInput.collectAsState()
    val isNumberActive = activeReminderInput?.type == remindType && activeReminderInput?.id == reminder.id

    Row(
        modifier = Modifier
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Input field
        TimeInput(
            value = reminder.value,
            onValueChange = { newValue: Int ->
                viewModel.updateReminder(remindType, reminder.id, value = newValue)
            },
            isActive = isNumberActive,
            onActiveChange = { active: Boolean ->
                if (active) {
                    viewModel.setActiveReminderInput(remindType, reminder.id)
                } else {
                    viewModel.clearReminderInputSelection()
                }
            }
        )

        Spacer(modifier = Modifier.width(Sizes.Size.L))

        // Dropdown
        TimeDropdown(
            selectedUnit = reminder.timeUnit,
            onUnitSelected = { newUnit: TimeUnit ->
                viewModel.updateReminder(remindType, reminder.id, timeUnit = newUnit)
            },
            onDropdownClick = {
                viewModel.clearReminderInputSelection()
            }
        )

        Spacer(modifier = Modifier.width(Sizes.Size.L))

        // Delete icon
        WithRipple {
            Box(
                modifier = Modifier
                    .clickableRippleDismiss(
                        onClick = { viewModel.removeReminder(remindType, reminder.id) },
                        radius = Sizes.Icon.M
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(Sizes.Icon.M),
                    painter = painterResource(R.drawable.delete),
                    contentDescription = STR.REMOVE_REMINDER
                )
            }
        }
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
                style = if (isActive) highlightedStyle else deselectedStyle
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
    val unitLabels = remember {
        mapOf(
            TimeUnit.MINUTES to STR.MINUTES,
            TimeUnit.HOURS to STR.HOURS,
            TimeUnit.DAYS to STR.DAYS,
            TimeUnit.WEEKS to STR.WEEKS,
            TimeUnit.MONTHS to STR.MONTHS
        )
    }

    Box(modifier = modifier) {
        Text(
            text = unitLabels[selectedUnit] ?: "",
            style = deselectedStyle,
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
                    textStyle = deselectedStyle,
                    spacing = Sizes.Icon.M
                )
            }
        }
    }
}
