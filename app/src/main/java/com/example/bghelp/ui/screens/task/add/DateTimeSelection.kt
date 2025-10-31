package com.example.bghelp.ui.screens.task.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import java.time.format.DateTimeFormatter

@Composable
fun DateTimeSelection(modifier: Modifier = Modifier,
    viewModel: AddTaskViewModel,
    whenSelection: WhenSelection
) {
    Column(modifier = modifier.fillMaxWidth()) {
        DateSection(viewModel)
        
        AddTaskSpacer()
        
        CalendarSection(viewModel)
        
        if (whenSelection == WhenSelection.AT_TIME) {
            TimeSection(viewModel)
        }
    }
}

@Composable
private fun DateSection(viewModel: AddTaskViewModel) {
    val fMonthYearDayShort = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    val dateStartSelection by viewModel.dateStartSelection.collectAsState()
    val dateEndSelection by viewModel.dateEndSelection.collectAsState()
    val activeDateField by viewModel.activeDateField.collectAsState()
    val isEndDateVisible by viewModel.isEndDateVisible.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.clickable {
                viewModel.toggleCalendarFromDateField(DateField.START)
            },
            text = dateStartSelection.format(fMonthYearDayShort),
            style = if (activeDateField == DateField.START) TextStyles.MainBlue.Bold.Medium else TextStyles.Default.Medium,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            modifier = Modifier
                .size(Sizes.Icon.Small)
                .clickable {
                    viewModel.toggleEndDateVisible()
                },
            painter = painterResource(R.drawable.double_arrow_right),
            contentDescription = if (isEndDateVisible) "Hide end date" else "Show end date"
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            modifier = Modifier
                .alpha(if (isEndDateVisible) 1f else 0f)
                .then(
                    if (isEndDateVisible) {
                        Modifier.clickable {
                            viewModel.toggleCalendarFromDateField(DateField.END)
                        }
                    } else {
                        Modifier
                    }
                ),
            text = dateEndSelection?.format(fMonthYearDayShort) ?: dateStartSelection.format(fMonthYearDayShort),
            style = if (activeDateField == DateField.END) TextStyles.MainBlue.Bold.Medium else TextStyles.Default.Medium
        )
    }
}

@Composable
private fun CalendarSection(viewModel: AddTaskViewModel) {
    val dateStartSelection by viewModel.dateStartSelection.collectAsState()
    val dateEndSelection by viewModel.dateEndSelection.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val activeDateField by viewModel.activeDateField.collectAsState()
    val isCalendarVisible by viewModel.isCalendarVisible.collectAsState()
    val isEndDateVisible by viewModel.isEndDateVisible.collectAsState()

    val shouldShowCalendar by remember(isCalendarVisible, activeDateField, isEndDateVisible) {
        derivedStateOf {
            isCalendarVisible && (activeDateField == DateField.START || (isEndDateVisible && activeDateField == DateField.END))
        }
    }

    if (shouldShowCalendar) {
        DateRangeCalendar(
            currentMonth = currentMonth,
            startDate = dateStartSelection,
            endDate = if (isEndDateVisible) (dateEndSelection ?: dateStartSelection) else dateStartSelection,
            onDayClicked = { clickedDate ->
                viewModel.onDayClicked(clickedDate)
            },
            onMonthChanged = { ym -> viewModel.setCurrentMonth(ym) },
            isRangeMode = isEndDateVisible
        )
    }
}

@Composable
private fun TimeSection(viewModel: AddTaskViewModel) {
    val timeStartSelection by viewModel.timeStartSelection.collectAsState()
    val timeEndSelection by viewModel.timeEndSelection.collectAsState()
    val activeTimeField by viewModel.activeTimeField.collectAsState()
    val activeTimeSegment by viewModel.activeTimeSegment.collectAsState()
    val isTimeRangeInvalid by viewModel.isTimeRangeInvalid.collectAsState()
    val isEndTimeVisible by viewModel.isEndTimeVisible.collectAsState()
    val keyboardDismissKey by viewModel.keyboardDismissKey.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeSelectionInput(
            time = timeStartSelection,
            timeField = TimeField.START,
            activeTimeField = activeTimeField,
            activeTimeSegment = activeTimeSegment,
            onTimeChanged = { viewModel.setTimeStart(it) },
            onTimeInputClicked = {
                viewModel.onTimeInputClicked()
            },
            onSegmentSelected = { field, segment ->
                viewModel.setActiveTimeInput(field, segment)
            },
            onSelectionCleared = {
                viewModel.clearTimeInputSelection()
            },
            keyboardDismissKey = keyboardDismissKey
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            modifier = Modifier
                .size(Sizes.Icon.Small)
                .clickable {
                    viewModel.toggleEndTimeVisible()
                },
            painter = painterResource(R.drawable.double_arrow_right),
            contentDescription = if (isEndTimeVisible) "Hide end time" else "Show end time"
        )

        Spacer(modifier = Modifier.width(16.dp))

        TimeSelectionInput(
            time = timeEndSelection ?: timeStartSelection.plusHours(1),
            timeField = TimeField.END,
            activeTimeField = activeTimeField,
            activeTimeSegment = activeTimeSegment,
            onTimeChanged = { if (isEndTimeVisible) viewModel.setTimeEnd(it) },
            onTimeInputClicked = {
                if (isEndTimeVisible) {
                    viewModel.onTimeInputClicked()
                }
            },
            onSegmentSelected = { field, segment ->
                if (isEndTimeVisible) {
                    viewModel.setActiveTimeInput(field, segment)
                }
            },
            onSelectionCleared = {
                if (isEndTimeVisible) {
                    viewModel.clearTimeInputSelection()
                }
            },
            keyboardDismissKey = keyboardDismissKey,
            isError = isTimeRangeInvalid && isEndTimeVisible,
            modifier = Modifier.alpha(if (isEndTimeVisible) 1f else 0f)
        )
    }
}

