package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRipple
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.theme.MainBlue
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun RepeatSelection(
    viewModel: AddTaskViewModel
) {
    val selectedDays by viewModel.selectedDays.collectAsState()
    val selectedWeeks by viewModel.selectedWeeks.collectAsState()

    DaySelectionRow(
        selectedDays = selectedDays,
        onDayToggle = { day -> viewModel.toggleDaySelection(day) }
    )

    AddTaskSpacerSmall()

    WeekSelectionRow(
        selectedWeeks = selectedWeeks,
        onWeekToggle = { week -> viewModel.toggleWeekSelection(week) }
    )
}

@Composable
fun DaySelectionRow(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val dayNumbers = listOf(1, 2, 3, 4, 5, 6, 7)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Sizes.Icon.ExtraSmall)
    ) {
        days.forEachIndexed { index, day ->
            DayChip(
                label = day,
                isSelected = selectedDays.contains(dayNumbers[index]),
                onClick = { onDayToggle(dayNumbers[index]) }
            )
        }
    }
}

@Composable
fun DayChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    WithRipple {
        Box(
            modifier = modifier
                .size(Sizes.Icon.Large)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isSelected) MainBlue else MainBlue.copy(alpha = 0.1f)
                )
                .clickableRipple(
                    onClick = onClick,
                    radius = Sizes.Icon.Large / 2
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = if (isSelected) TextStyles.White.Bold.Medium else TextStyles.Default.Medium
            )
        }
    }
}

@Composable
fun WeekSelectionRow(
    selectedWeeks: Set<Int>,
    onWeekToggle: (Int) -> Unit
) {
    val weeks = listOf(1, 2, 3, 4)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Sizes.Icon.ExtraSmall)
    ) {
        weeks.forEach { week ->
            WeekChip(
                label = week.toString(),
                isSelected = selectedWeeks.contains(week),
                onClick = { onWeekToggle(week) }
            )
        }
    }
}

@Composable
fun WeekChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    WithRipple {
        Box(
            modifier = modifier
                .size(Sizes.Icon.Large)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isSelected) MainBlue else MainBlue.copy(alpha = 0.1f)
                )
                .clickableRipple(
                    onClick = onClick,
                    radius = Sizes.Icon.Large / 2
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = if (isSelected) TextStyles.White.Bold.Medium else TextStyles.Default.Medium
            )
        }
    }
}

