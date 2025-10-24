package com.example.bghelp.ui.screens.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.ui.components.Day
import com.example.bghelp.utils.AlarmMode
import java.time.Instant
import java.time.ZoneId

@Composable
fun TaskScreen(taskViewModel: TaskViewModel = hiltViewModel()) {
    val tasks by taskViewModel.tasksInRange.collectAsState(initial = emptyList())
    // val allTasks by taskViewModel.getAllTasks.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Column {
            AddButtonRow(taskViewModel)

            if (tasks.isNotEmpty()) {
                Day(tasks, onDelete = { task -> taskViewModel.deleteTask(task) })
            }
        }
    }
}

@Composable
fun AddButtonRow(taskViewModel: TaskViewModel) {
    val now: Long = 0
    val oneMinute: Long = 60 * 1000 // 60 seconds in milliseconds
    val oneHour: Long = 60 * 60 * 1000 // 1 hour in milliseconds
    val oneDay: Long = 24 * 60 * 60 * 1000 // 1 day in milliseconds
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AddTaskButton(taskViewModel = taskViewModel, time = now, text = "Now")
        AddTaskButton(taskViewModel = taskViewModel, time = oneMinute, text = "Min")
        AddTaskButton(taskViewModel = taskViewModel, time = oneHour, text = "Hour")
        AddTaskButton(taskViewModel = taskViewModel, time = oneDay, text = "Day")
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
            val now: Long = Instant.now().toEpochMilli() // Use milliseconds
            val date = now + time
            val localDateTime = Instant.ofEpochMilli(date) // Use ofEpochMilli
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
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