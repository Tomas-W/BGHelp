package com.example.bghelp.ui.screens.target

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.domain.model.CreateTarget
import com.example.bghelp.ui.components.LazyColumnContainer
import com.example.bghelp.ui.components.MainHeader
import com.example.bghelp.ui.components.Item
import com.example.bghelp.ui.components.WeekNavigationRow
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.Coordinate
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.screens.task.EmptyTask
import com.example.bghelp.utils.toDayHeader
import java.time.LocalDateTime

@Composable
fun TargetScreen(targetViewModel: TargetViewModel = hiltViewModel()) {
    val selectedTargets by targetViewModel.targetsInRange.collectAsState(initial = emptyList())
    val selectedDate by targetViewModel.selectedDate.collectAsState()
    val selectedDateOnly = selectedDate.toLocalDate()
    val monthYear by targetViewModel.monthYear.collectAsState()
    val weekNumber by targetViewModel.weekNumber.collectAsState()
    val weekDays by targetViewModel.weekDays.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

//        AddButtonRow(targetViewModel)

        WeekNavigationRow(
            monthYear = monthYear,
            weekNumber = weekNumber,
            weekDays = weekDays,
            selectedDate = selectedDateOnly,
            onPreviousWeek = { targetViewModel.goToPreviousWeek() },
            onNextWeek = { targetViewModel.goToNextWeek() },
            onDaySelected = targetViewModel::goToDay,
        )
        MainContentContainer {
            MainHeader(selectedDate.toDayHeader())

            LazyColumnContainer {
                if (selectedTargets.isNotEmpty()) {
                    items(selectedTargets) { task ->
                        Item(task, onDelete = { targetViewModel.deleteTarget(task) })
                    }
                } else {
                    item {
                        EmptyTask()
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTarget() {
    Text("No targets for this day")
}

@Composable
fun AddButtonRow(targetViewModel: TargetViewModel) {
    val now: LocalDateTime = LocalDateTime.now()
    val oneHour: LocalDateTime = now.plusHours(1)
    val oneDay: LocalDateTime = now.plusDays(1)
    val threeDays: LocalDateTime = now.plusDays(3)
    val oneWeek: LocalDateTime = now.plusWeeks(1)

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // AddTargetButton(targetViewModel = targetViewModel, time = now, text = "Now")
        // AddTargetButton(targetViewModel = targetViewModel, time = oneMinute, text = "Min")
        AddTargetButton(targetViewModel = targetViewModel, date = oneHour, text = "Hour")
        AddTargetButton(targetViewModel = targetViewModel, date = oneDay, text = "Day")
        AddTargetButton(targetViewModel = targetViewModel, date = threeDays, text = "3 Days")
        AddTargetButton(targetViewModel = targetViewModel, date = oneWeek, text = "Week")
    }
}

@Composable
fun AddTargetButton(
    targetViewModel: TargetViewModel,
    date: LocalDateTime,
    text: String
) {
    Button(
        onClick = {
            val coords = listOf(Coordinate(53.5, -4.7))
            targetViewModel.addTarget(
                CreateTarget(
                    date = date,
                    message = "Test Target $date",
                    expired = false,
                    coordinates = coords,
                    alertDistance = 300,
                    alarmName = "whatever",
                    sound = AlarmMode.ONCE,
                    vibrate = AlarmMode.CONTINUOUS,
                    snoozeTime = 0
                )
            )
        }
    ) {
        Text(text)
    }
}
