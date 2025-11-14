package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRipple
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.deselectedStyle
import com.example.bghelp.ui.screens.task.add.highlightedStyle
import com.example.bghelp.ui.screens.task.add.selectedStyle
import com.example.bghelp.ui.theme.Sizes

@Composable
fun WeeklySelection(
    viewModel: AddTaskViewModel
) {
    val selectedDays by viewModel.weeklySelectedDays.collectAsState()
    val selectedWeek by viewModel.weeklyIntervalWeeks.collectAsState()

    DaySelection(
        selectedDays = selectedDays,
        onDayToggle = { day -> viewModel.toggleWeeklySelectedDays(day) }
    )

    AddTaskSpacerSmall()

    WeekSelection(
        selectedWeek = selectedWeek,
        onWeekSelected = { week -> viewModel.setWeeklyIntervalWeeks(week) }
    )
}

@Composable
private fun DaySelection(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val dayNumbers = listOf(1, 2, 3, 4, 5, 6, 7)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = CONST.START_PADDING.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { i, day ->
            val isSelected = selectedDays.contains(dayNumbers[i])
            Text(
                modifier = Modifier
                    .clickable { onDayToggle(dayNumbers[i]) },
                text = day,
                style = if (isSelected) selectedStyle else deselectedStyle,
            )
        }
    }
}

@Composable
private fun WeekSelection(
    selectedWeek: Int,
    onWeekSelected: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Every",
            style = deselectedStyle
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.S))

        WeekInput(
            value = selectedWeek,
            onValueChange = onWeekSelected,
            minValue = 1,
            maxValue = 99
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.S))

        Text(
            text = "weeks",
            style = deselectedStyle
        )
    }
}

@Composable
private fun WeekInput(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int = 1,
    maxValue: Int = 99
) = WithRipple {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var inputBuffer by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }

    fun dismissKeyboard() {
        isActive = false
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    fun clampValue(value: Int): Int = value.coerceIn(minValue, maxValue)

    fun handleInput(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(2)
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
                    isActive = false
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
            .width(50.dp)
            .clip(RoundedCornerShape(Sizes.Corner.S))
            .clickable {
                if (isActive) {
                    dismissKeyboard()
                } else {
                    isActive = true
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
                        isActive = true
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
