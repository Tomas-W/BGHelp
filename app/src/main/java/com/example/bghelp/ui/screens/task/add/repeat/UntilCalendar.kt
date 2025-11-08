package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bghelp.ui.components.DateRangeCalendar
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.RepeatUntilContext
import java.time.LocalDate

@Composable
fun UntilCalendar(
    viewModel: AddTaskViewModel
) {
    val weeklyUntilDate by viewModel.weeklyUntilDate.collectAsState()
    val monthlyUntilDate by viewModel.monthlyUntilDate.collectAsState()
    val currentMonthUntil by viewModel.untilCalendarMonth.collectAsState()
    val activeContext by viewModel.activeUntilCalendarContext.collectAsState()

    val selectedDate = when (activeContext) {
        RepeatUntilContext.WEEKLY -> weeklyUntilDate
        RepeatUntilContext.MONTHLY -> monthlyUntilDate
        null -> weeklyUntilDate
    }

    DateRangeCalendar(
        currentMonth = currentMonthUntil,
        startDate = selectedDate,
        endDate = selectedDate,
        onDayClicked = { clickedDate ->
            viewModel.onUntilDayClicked(clickedDate)
        },
        onMonthChanged = { ym -> viewModel.setUntilCalendarMonth(ym) },
        isRangeMode = false,
        minDate = LocalDate.now()
    )
}

