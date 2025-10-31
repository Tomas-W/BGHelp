package com.example.bghelp.ui.screens.task.add.date

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
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableWithUnboundedRipple
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.DateField
import com.example.bghelp.ui.screens.task.add.TimeField
import com.example.bghelp.ui.screens.task.add.UserDateSelection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import java.time.format.DateTimeFormatter

@Composable
fun DateTimeSelection(modifier: Modifier = Modifier,
                      viewModel: AddTaskViewModel,
                      userDateSelection: UserDateSelection
) {
    Column(modifier = modifier.fillMaxWidth()) {
        DateSelection(viewModel)

        CalendarSection(viewModel)
        
        if (userDateSelection == UserDateSelection.AT_TIME) {
            AddTaskSpacerSmall()
            TimeSelection(viewModel)
        }
    }
}

@Composable
private fun DateSelection(viewModel: AddTaskViewModel) {
    val fMonthYearDayShort = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    val dateStartSelection by viewModel.dateStartSelection.collectAsState()
    val dateEndSelection by viewModel.dateEndSelection.collectAsState()
    val activeDateField by viewModel.activeDateField.collectAsState()
    val isEndDateVisible by viewModel.isEndDateVisible.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(Sizes.Icon.Medium + Sizes.Icon.Large))

        // startDate
        Text(
            modifier = Modifier
                .widthIn(min = AddTaskConstants.MIN_WIDTH.dp)
                .clickable {
                viewModel.toggleCalendarFromDateField(DateField.START)
            },
            text = dateStartSelection.format(fMonthYearDayShort),
            style = if (activeDateField == DateField.START) TextStyles.MainBlue.Bold.Medium else TextStyles.Default.Medium,
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Medium))

        // Double arrow
        WithRipple {
            Box(
                modifier = Modifier
                    .clickableWithUnboundedRipple(
                        onClick = {
                            viewModel.toggleEndDateVisible()
                        },
                        radius = Sizes.Icon.Small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(Sizes.Icon.Small),
                    painter = painterResource(R.drawable.double_arrow_right),
                    contentDescription = if (isEndDateVisible) "Hide end date" else "Show end date"
                )
            }
        }

        Spacer(modifier = Modifier.width(Sizes.Icon.Medium))

        // endDate
        Text(
            modifier = Modifier
                // Hide if no endDate
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
private fun TimeSelection(viewModel: AddTaskViewModel) {
    val timeStartSelection by viewModel.timeStartSelection.collectAsState()
    val timeEndSelection by viewModel.timeEndSelection.collectAsState()
    val activeTimeField by viewModel.activeTimeField.collectAsState()
    val activeTimeSegment by viewModel.activeTimeSegment.collectAsState()
    val isTimeRangeInvalid by viewModel.isTimeRangeInvalid.collectAsState()
    val isEndTimeVisible by viewModel.isEndTimeVisible.collectAsState()
    val keyboardDismissKey by viewModel.keyboardDismissKey.collectAsState()

    val iconPaddingOffset = 8.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(Sizes.Icon.Medium + Sizes.Icon.Large - iconPaddingOffset))

        TimeSelectionInput(
            modifier = Modifier.widthIn(min = AddTaskConstants.MIN_WIDTH.dp + iconPaddingOffset),
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

        Spacer(modifier = Modifier.width(Sizes.Icon.Medium))

        WithRipple {
            Box(
                modifier = Modifier
                    .clickableWithUnboundedRipple(
                        onClick = {
                            viewModel.toggleEndTimeVisible()
                        },
                        radius = Sizes.Icon.Small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(Sizes.Icon.Small),
                    painter = painterResource(R.drawable.double_arrow_right),
                    contentDescription = if (isEndTimeVisible) "Hide end time" else "Show end time"
                )
            }
        }

        Spacer(modifier = Modifier.width(Sizes.Icon.Medium))

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

