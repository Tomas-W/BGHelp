package com.example.bghelp.ui.screens.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.data.local.TaskEntity
import com.example.bghelp.ui.screens.task.components.Day
import com.example.bghelp.utils.AlarmMode
import java.time.Instant


@Composable
fun TaskScreen(taskViewModel: TaskViewModel = hiltViewModel()) {
    val tasks by taskViewModel.getAllTasks.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
    ) {
        Column {
            AddTaskButton(taskViewModel = taskViewModel)
            if (tasks.isNotEmpty()) {
                Day(tasks)
            }
        }
    }
}


@Composable
fun AddTaskButton(taskViewModel: TaskViewModel) {
        Button(
        onClick = {
            val now: Long = Instant.now().epochSecond
            taskViewModel.addTask(TaskEntity(
                date = now,
                message = "Test Task 1",
                expired = false,
                alarmName = "AlarmOne",
                sound = AlarmMode.CONTINUOUS,
                vibrate = AlarmMode.OFF,
                snoozeTime = 0
            ))
        },

    ) {
        Text("Add Task")
    }
}