package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.RepeatMonthlyDaySelection
import com.example.bghelp.ui.screens.task.add.deselectedStyle
import com.example.bghelp.ui.screens.task.add.selectedStyle
import com.example.bghelp.ui.theme.Sizes
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthlySelection(
    viewModel: AddTaskViewModel
) {
    val selectedMonths by viewModel.monthlySelectedMonths.collectAsState()
    val selectedMonthDays by viewModel.monthlySelectedDays.collectAsState()

    MonthSelection(
        viewModel = viewModel,
        selectedMonths = selectedMonths,
        onToggle = { month -> viewModel.toggleMonthlySelectedMonths(month) }
    )

    AddTaskSpacerMedium()

    DaySelection(
        viewModel = viewModel,
        selectedDays = selectedMonthDays,
        onToggle = { day -> viewModel.toggleMonthSelectedDays(day) }
    )
}

@Composable
private fun MonthSelection(
    viewModel: AddTaskViewModel,
    selectedMonths: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val months = remember { Month.entries }
    val monthChunks = remember(months) { months.chunked(4) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Sizes.Size.XS)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                modifier = Modifier
                    .clickable { viewModel.selectAllMonthlySelectedMonths() },
                text = STR.SELECT_ALL,
                style = deselectedStyle
            )

            Text(
                modifier = Modifier
                    .clickable { viewModel.deselectAllMonthlySelectedMonths() },
                text = STR.DESELECT_ALL,
                style = deselectedStyle
            )
        }

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
                                selectedStyle
                            } else {
                                deselectedStyle
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
    viewModel: AddTaskViewModel,
    selectedDays: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val monthlyDaySelection by viewModel.userMonthlyDaySelection.collectAsState()
    val allMonthDaysSelected by viewModel.allMonthDaysSelected.collectAsState()

    val dayNumbers = remember { (1..31).toList() }
    val dayChunks = remember(dayNumbers) { dayNumbers.chunked(8) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Sizes.Size.XS)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                modifier = Modifier
                    .clickable { viewModel.toggleMonthlyDaySelection(RepeatMonthlyDaySelection.ALL)},
                text = "All days",
                style = if (monthlyDaySelection == RepeatMonthlyDaySelection.ALL) selectedStyle else deselectedStyle
            )

            Text(
                modifier = Modifier
                    .clickable {
                        viewModel.toggleMonthlyDaySelection(RepeatMonthlyDaySelection.SELECT)
                               },
                text = "Select days",
                style = if (monthlyDaySelection == RepeatMonthlyDaySelection.SELECT) selectedStyle else deselectedStyle
            )

            Text(
                modifier = Modifier
                    .clickable { viewModel.toggleMonthlyDaySelection(RepeatMonthlyDaySelection.LAST)},
                text = "Last of month",
                style = if (monthlyDaySelection == RepeatMonthlyDaySelection.LAST) selectedStyle else deselectedStyle
            )
        }

        if (monthlyDaySelection == RepeatMonthlyDaySelection.SELECT) {
            dayChunks.forEach { chunk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Sizes.Size.S)
                ) {
                    chunk.forEach {day ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                modifier = Modifier.clickable { onToggle(day) },
                                text = day.toString(),
                                style = if (selectedDays.contains(day)) selectedStyle else deselectedStyle
                            )
                        }
                        // Hack to align since 31/8 != Int
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    modifier = Modifier
                        .clickable { viewModel.selectAllMonthlySelectedDays() },
                    text = "Select all",
                    style = deselectedStyle
                )

                Text(
                    modifier = Modifier
                        .clickable { viewModel.deselectAllMonthlySelectedDays() },
                    text = "Deselect all",
                    style = deselectedStyle
                )

            }
        }

    }
}
