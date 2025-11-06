package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.UserDateSelection
import com.example.bghelp.ui.screens.task.add.DateField
import com.example.bghelp.ui.screens.task.add.UserRepeatSelection

@Composable
fun DateSection(
    viewModel: AddTaskViewModel
) {
    val userDateSelection by viewModel.userDateSelection.collectAsState()
    val userRepeatSelection by viewModel.userRepeatSelection.collectAsState()

    val activeDateField by viewModel.activeDateField.collectAsState()
    val isCalendarVisible by viewModel.isCalendarVisible.collectAsState()
    val isEndDateVisible by viewModel.isEndDateVisible.collectAsState()

    val shouldShowCalendar by remember(isCalendarVisible, activeDateField, isEndDateVisible) {
        derivedStateOf {
            isCalendarVisible && (activeDateField == DateField.START || (isEndDateVisible && activeDateField == DateField.END))
        }
    }

    Header(
        viewModel = viewModel,
        userSectionSelection = userDateSelection,
        toggleSection = { viewModel.toggleDateSelection() }
    )

    if (userRepeatSelection == UserRepeatSelection.OFF) {
        SubContainer {
            DateSelection(
                viewModel = viewModel
            )
        }
    }


    if (shouldShowCalendar) {
        CalendarSelection(
            viewModel = viewModel
        )
    }

    if (userDateSelection == UserDateSelection.OFF) {
        TimeSelection(
            viewModel = viewModel
        )
    }
}
