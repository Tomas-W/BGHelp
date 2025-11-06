package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun RepeatSelection(
    viewModel: AddTaskViewModel
) {
    val selectedDays by viewModel.selectedDays.collectAsState()
    val selectedWeek by viewModel.selectedWeek.collectAsState()

    DaySelection(
        selectedDays = selectedDays,
        onDayToggle = { day -> viewModel.toggleDaySelection(day) }
    )

    AddTaskSpacerSmall()

    WeekSelection(
        selectedWeek = selectedWeek,
        onWeekSelected = { week -> viewModel.setWeekSelection(week) }
    )
}

@Composable
fun DaySelection(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val dayNumbers = listOf(1, 2, 3, 4, 5, 6, 7)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = AddTaskConstants.START_PADDING.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { i, day ->
            val isSelected = selectedDays.contains(dayNumbers[i])
            Text(
                modifier = Modifier
                    .clickable { onDayToggle(dayNumbers[i]) },
                text = day,
                style = if (isSelected) TextStyles.Default.Bold.Medium else TextStyles.Default.Medium,
            )
        }
    }
}

@Composable
fun WeekSelection(
    selectedWeek: Int,
    onWeekSelected: (Int) -> Unit
) {
    val weeks = listOf(" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 " , " 9 " , " 10 ")
    val weekNumbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    var isExpanded by remember { mutableStateOf(false) }

    val displayText = weeks[selectedWeek - 1]

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Every",
            style = TextStyles.Default.Medium
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Small))

        Box(
            modifier = Modifier.wrapContentWidth()
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        isExpanded = true
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayText,
                    style = TextStyles.Default.Bold.Medium.copy(
                        textDecoration = TextDecoration.Underline
                    )
                )
            }

            CustomDropdown(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                weekNumbers.forEachIndexed { index, weekNumber ->
                    val isSelected = selectedWeek == weekNumber
                    DropdownItem(
                        label = weeks[index],
                        onClick = {
                            onWeekSelected(weekNumber)
                            isExpanded = false
                        },
                        textStyle = (if (isSelected) {
                            TextStyles.Default.Bold.Medium
                        } else {
                            TextStyles.Default.Medium
                        }).copy(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(Sizes.Icon.Small))

        Text(
            text = "weeks",
            style = TextStyles.Default.Medium
        )
    }
}

@Composable
fun UntilSelection() {

}
