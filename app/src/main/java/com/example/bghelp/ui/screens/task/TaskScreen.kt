package com.example.bghelp.ui.screens.task

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
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.ui.components.LazyColumnContainer
import com.example.bghelp.ui.components.MainHeader
import com.example.bghelp.ui.components.Item
import com.example.bghelp.ui.components.WeekNavigationRow
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.utils.toDayHeader
import java.time.LocalDateTime

@Composable
fun TaskScreen(taskViewModel: TaskViewModel = hiltViewModel()) {
    val selectedTasks by taskViewModel.tasksInRange.collectAsState(initial = emptyList())
    val selectedDate by taskViewModel.selectedDate.collectAsState()
    val selectedDateOnly = selectedDate.toLocalDate()
    val monthYear by taskViewModel.monthYear.collectAsState()
    val weekNumber by taskViewModel.weekNumber.collectAsState()
    val weekDays by taskViewModel.weekDays.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
        ) {
        
//        AddButtonRow(taskViewModel)

        WeekNavigationRow(
            monthYear = monthYear,
            weekNumber = weekNumber,
            weekDays = weekDays,
            selectedDate = selectedDateOnly,
            onPreviousWeek = { taskViewModel.goToPreviousWeek() },
            onNextWeek = { taskViewModel.goToNextWeek() },
            onDaySelected = taskViewModel::goToDay,
        )
        MainContentContainer {
            MainHeader(selectedDate.toDayHeader())

            LazyColumnContainer {
                if (selectedTasks.isNotEmpty()) {
                    items(selectedTasks) { task ->
                        Item(task, onDelete = { taskViewModel.deleteTask(task) })
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
fun EmptyTask() {
    Text("No tasks for this day")
}

@Composable
fun AddButtonRow(taskViewModel: TaskViewModel) {
    val now: Long = 0
    val oneMinute: Long = 60 * 1000
    val oneHour: Long = 60 * 60 * 1000
    val oneDay: Long = 24 * 60 * 60 * 1000
    val threeDays: Long = 3 * 24 * 60 * 60 * 1000
    val oneWeek: Long = 7 * 24 * 60 * 60 * 1000

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // AddTaskButton(taskViewModel = taskViewModel, time = now, text = "Now")
        // AddTaskButton(taskViewModel = taskViewModel, time = oneMinute, text = "Min")
       AddTaskButton(taskViewModel = taskViewModel, time = oneHour, text = "Hour")
        AddTaskButton(taskViewModel = taskViewModel, time = oneDay, text = "Day")
        AddTaskButton(taskViewModel = taskViewModel, time = threeDays, text = "3 Days")
        AddTaskButton(taskViewModel = taskViewModel, time = oneWeek, text = "Week")
    }
}

@Composable
fun AddTaskButton(
    taskViewModel: TaskViewModel,
    time: Long,
    text: String
) {
    Button(
        onClick = {
            val now = LocalDateTime.now()
            val localDateTime = now.plusSeconds(time / 1000)
            taskViewModel.addTask(
                CreateTask(
                    date = localDateTime,
                    message = "Test Task $localDateTime",
                    expired = false,
                    alarmName = "AlarmOne",
                    sound = AlarmMode.CONTINUOUS,
                    vibrate = AlarmMode.ONCE,
                    snoozeTime = 55
                )
            )
        }
    ) {
        Text(text)
    }
}
