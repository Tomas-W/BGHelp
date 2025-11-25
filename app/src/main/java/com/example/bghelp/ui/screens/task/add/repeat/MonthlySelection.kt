package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.bghelp.ui.components.deselectedTextStyle
import com.example.bghelp.ui.components.selectedTextStyle
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerLarge
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.RepeatMonthlyDaySelection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.lTextBold
import com.example.bghelp.ui.theme.lTextDefault
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR

@Composable
fun MonthlySelection(
    viewModel: AddTaskViewModel
) {
    val selectedMonths by viewModel.monthlySelectedMonths.collectAsState()
    val selectedMonthDays by viewModel.monthlySelectedDays.collectAsState()

    AddTaskSpacerSmall()

    MonthSelection(
        viewModel = viewModel,
        selectedMonths = selectedMonths,
        onToggle = { month -> viewModel.toggleMonthlySelectedMonths(month) }
    )

    AddTaskSpacerLarge()

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
                style = MaterialTheme.typography.lTextDefault
            )

            Text(
                modifier = Modifier
                    .clickable { viewModel.deselectAllMonthlySelectedMonths() },
                text = STR.DESELECT_ALL,
                style = MaterialTheme.typography.lTextDefault
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
                    val selected = selectedMonths.contains(monthNumber)
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.clickable { onToggle(monthNumber) },
                            text = label,
                            style = if (selected) {
                                MaterialTheme.typography.lTextBold
                            } else {
                                MaterialTheme.typography.lTextDefault
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
                    .clickable { viewModel.toggleMonthlyDaySelection(RepeatMonthlyDaySelection.ALL) },
                text = STR.ALL_DAYS,
                style = if (monthlyDaySelection == RepeatMonthlyDaySelection.ALL) {
                    selectedTextStyle()
                } else {
                    deselectedTextStyle()
                }
            )

            Text(
                modifier = Modifier
                    .clickable {
                        viewModel.toggleMonthlyDaySelection(RepeatMonthlyDaySelection.SELECT)
                    },
                text = STR.SELECT_DAYS,
                style = if (monthlyDaySelection == RepeatMonthlyDaySelection.SELECT) {
                    selectedTextStyle()
                } else {
                    deselectedTextStyle()
                }
            )

            Text(
                modifier = Modifier
                    .clickable { viewModel.toggleMonthlyDaySelection(RepeatMonthlyDaySelection.LAST) },
                text = STR.LAST_OF_MONTH,
                style = if (monthlyDaySelection == RepeatMonthlyDaySelection.LAST) {
                    selectedTextStyle()
                } else {
                    deselectedTextStyle()
                }
            )
        }

        if (monthlyDaySelection == RepeatMonthlyDaySelection.SELECT) {
            dayChunks.forEach { chunk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Sizes.Size.S)
                ) {
                    chunk.forEach { day ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                modifier = Modifier.clickable { onToggle(day) },
                                text = day.toString(),
                                style = if (selectedDays.contains(day)) {
                                    selectedTextStyle()
                                } else {
                                    deselectedTextStyle()
                                }
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
                    text = STR.SELECT_ALL,
                    style = MaterialTheme.typography.lTextDefault
                )

                Text(
                    modifier = Modifier
                        .clickable { viewModel.deselectAllMonthlySelectedDays() },
                    text = STR.DESELECT_ALL,
                    style = MaterialTheme.typography.lTextDefault
                )

            }
        }

    }
}
