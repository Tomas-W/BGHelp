package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.UserRepeatSelection
import com.example.bghelp.ui.screens.task.add.RepeatUntilContext
import com.example.bghelp.ui.screens.task.add.RepeatUntilSelection

@Composable
fun RepeatSection(
    viewModel: AddTaskViewModel
) {
    val userRepeatSelection by viewModel.userRepeatSelection.collectAsState()
    val selectedWeeklyUntil by viewModel.weeklyUntilSelection.collectAsState()
    val selectedMonthlyUntil by viewModel.monthlyUntilSelection.collectAsState()
    val isUntilCalendarVisible by viewModel.isUntilCalendarVisible.collectAsState()
    val activeUntilCalendarContext by viewModel.activeUntilCalendarContext.collectAsState()

    Header(
        viewModel = viewModel,
        userSectionSelection = userRepeatSelection,
        toggleSection = { viewModel.toggleRepeatSelection() }
    )

    when (userRepeatSelection) {
        UserRepeatSelection.WEEKLY -> {
            SubContainer {
                RepeatSelection(
                    viewModel = viewModel
                )
            }

            val shouldShowUntilCalendar = isUntilCalendarVisible &&
                activeUntilCalendarContext == RepeatUntilContext.WEEKLY &&
                selectedWeeklyUntil == RepeatUntilSelection.DATE
            if (shouldShowUntilCalendar) {
                UntilCalendarSelection(
                    viewModel = viewModel
                )
            }
        }
        UserRepeatSelection.MONTHLY -> {
            SubContainer {
                RepeatMonthlySelection(
                    viewModel = viewModel
                )
            }

            val shouldShowUntilCalendar = isUntilCalendarVisible &&
                activeUntilCalendarContext == RepeatUntilContext.MONTHLY &&
                selectedMonthlyUntil == RepeatUntilSelection.DATE
            if (shouldShowUntilCalendar) {
                UntilCalendarSelection(
                    viewModel = viewModel
                )
            }
        }
        else -> {}
    }
}