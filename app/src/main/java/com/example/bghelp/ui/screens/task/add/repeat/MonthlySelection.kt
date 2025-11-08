package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.RepeatUntilSelection
import com.example.bghelp.ui.theme.SecondaryBlue
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import java.time.format.DateTimeFormatter

@Composable
fun MonthlySelection(
    viewModel: AddTaskViewModel
) {
    val selectedMonths by viewModel.monthlySelectedMonths.collectAsState()
    val selectedMonthDays by viewModel.monthlySelectedDays.collectAsState()

    MonthSelection(
        selectedMonths = selectedMonths,
        onToggle = { month -> viewModel.toggleMonthSelection(month) }
    )

    AddTaskSpacerSmall()

    DaySelection(
        selectedDays = selectedMonthDays,
        onToggle = { day -> viewModel.toggleMonthDaySelection(day) }
    )

//    AddTaskSpacerSmall()
//
//    UntilSelection(
//        selectedUntil = selectedMonthlyUntil,
//        selectedDate = selectedMonthlyDate,
//        onForever = { viewModel.setMonthlyUntilSelection(RepeatUntilSelection.FOREVER) },
//        onDateClick = {
//            viewModel.setMonthlyUntilSelection(RepeatUntilSelection.DATE)
//            viewModel.toggleMonthlyUntilCalendar()
//        }
//    )
}

@Composable
private fun MonthSelection(
    selectedMonths: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val months = remember { Month.entries }
    val monthChunks = remember(months) { months.chunked(4) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Sizes.Size.ExtraSmall)
    ) {
        monthChunks.forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                chunk.forEach { month ->
                    val monthNumber = month.value
                    val label = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.clickable { onToggle(monthNumber) },
                            text = label,
                            style = if (selectedMonths.contains(monthNumber)) {
                                TextStyles.Default.Bold.Medium
                            } else {
                                TextStyles.Default.Medium
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DaySelection(
    selectedDays: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val dayNumbers = remember { (1..31).toList() }
    val dayChunks = remember(dayNumbers) { dayNumbers.chunked(8) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Sizes.Size.ExtraSmall)
    ) {
        dayChunks.forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Sizes.Size.Small)
            ) {
                chunk.forEach {day ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.clickable { onToggle(day) },
                            text = day.toString(),
                            style = if (selectedDays.contains(day)) {
                                TextStyles.Default.Bold.Medium
                            } else {
                                TextStyles.Default.Medium
                            }
                        )
                    }
                    if (day == 31) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                text = ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UntilSelection(
    selectedUntil: RepeatUntilSelection,
    selectedDate: java.time.LocalDate,
    onForever: () -> Unit,
    onDateClick: () -> Unit
) {
    val fMonthYearDayShort = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = AddTaskConstants.START_PADDING.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Until",
            style = TextStyles.Default.Medium
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Medium))

        Text(
            modifier = Modifier.clickable {
                onForever()
            },
            text = "Forever",
            style = if (selectedUntil == RepeatUntilSelection.FOREVER) {
                TextStyles.Default.Bold.Medium
            } else {
                TextStyles.Default.Medium
            }
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Medium))

        Text(
            modifier = Modifier.clickable {
                onDateClick()
            },
            text = selectedDate.format(fMonthYearDayShort),
            style = if (selectedUntil == RepeatUntilSelection.DATE) {
                TextStyles.Default.Bold.Medium
            } else {
                TextStyles.Default.Medium
            }
        )
    }
}
