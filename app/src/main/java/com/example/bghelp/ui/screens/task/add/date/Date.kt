package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.AddTaskHeader
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.UserDateSelection
import com.example.bghelp.ui.screens.task.add.DateField

@Composable
fun DateSection(
    viewModel: AddTaskViewModel
) {
    val userDateSelection by viewModel.userDateSelection.collectAsState()
    val dateStringChoices = remember { mapOf(
        UserDateSelection.AT_TIME to AddTaskStrings.AT_TIME,
        UserDateSelection.ALL_DAY to AddTaskStrings.ALL_DAY) }
    val dateIconChoices = remember { mapOf(
        UserDateSelection.AT_TIME to R.drawable.at_time,
        UserDateSelection.ALL_DAY to R.drawable.all_day) }

    val activeDateField by viewModel.activeDateField.collectAsState()
    val isCalendarVisible by viewModel.isCalendarVisible.collectAsState()
    val isEndDateVisible by viewModel.isEndDateVisible.collectAsState()

    val shouldShowCalendar by remember(isCalendarVisible, activeDateField, isEndDateVisible) {
        derivedStateOf {
            isCalendarVisible && (activeDateField == DateField.START || (isEndDateVisible && activeDateField == DateField.END))
        }
    }

    AddTaskHeader(
        viewModel = viewModel,
        userSectionSelection = userDateSelection,
        stringChoices = dateStringChoices,
        iconChoices = dateIconChoices,
        toggleSection = { viewModel.toggleDateSelection() }
    )

    AddTaskSpacerMedium()

    DateSelection(
        viewModel = viewModel
    )

    if (shouldShowCalendar) {
        CalendarSelection(
            viewModel = viewModel
        )
    }

    if (userDateSelection == UserDateSelection.AT_TIME) {
        TimeSelection(
            viewModel = viewModel
        )
    }
}
