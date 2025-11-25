package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bghelp.ui.components.DateRangeCalendar
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import java.time.LocalDate

@Composable
fun CalendarSelection(
    viewModel: AddTaskViewModel
) {
    val dateStartSelection by viewModel.dateStartSelection.collectAsState()
    val dateEndSelection by viewModel.dateEndSelection.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val isEndDateVisible by viewModel.isEndDateVisible.collectAsState()

    DateRangeCalendar(
        currentMonth = currentMonth,
        startDate = dateStartSelection,
        endDate = if (isEndDateVisible) (dateEndSelection?: dateStartSelection)
                  else dateStartSelection,
        onDayClicked = { clickedDate ->
            viewModel.onDayClicked(clickedDate)
        },
        onMonthChanged = { ym -> viewModel.setCurrentMonth(ym) },
        isRangeMode = isEndDateVisible,
        minDate = LocalDate.now(),
        onDismissRequest = { viewModel.setCalendarVisible(false) },
        onInvalidDateClicked = { clickedDate ->
            viewModel.showSnackbar(AddTaskStrings.DATE_CANNOT_BE_IN_PAST)
        },
        selectedDate = if (!isEndDateVisible) dateStartSelection else null
    )
}
