package com.example.bghelp.ui.screens.task.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.Task
import com.example.bghelp.ui.components.LazyColumnContainer
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.MainHeader
import com.example.bghelp.ui.components.WeekNavigation
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.utils.toDayHeader
import com.example.bghelp.utils.toTaskTime
import java.time.LocalDateTime

@SuppressLint("FrequentlyChangingValue", "FrequentlyChangedStateReadInComposition")
@Composable
fun TaskScreen(taskViewModel: TaskViewModel = hiltViewModel()) {
    // Task items
    val selectedTasks by taskViewModel.tasksInRange.collectAsState(initial = emptyList())
    val selectedDate by taskViewModel.selectedDate.collectAsState()
    val expandedTaskIds by taskViewModel.expandedTaskIds.collectAsState()
    // Week navigation
    val rawSelectedDate by remember { derivedStateOf { selectedDate.toLocalDate() } }
    val navYearMonth by taskViewModel.currentYearMonth.collectAsState()
    val navWeekDisplay by taskViewModel.weekDisplay.collectAsState()
    val navWeekNumbers by taskViewModel.availableWeekNumbers.collectAsState()
    val navWeekDays by taskViewModel.weekDays.collectAsState()
    val navSelectedDate = remember(rawSelectedDate, navWeekDays) {
        navWeekDays.firstOrNull { it == rawSelectedDate }
    }
    // Scroll state
    val listState = rememberLazyListState()
    val savedIndex = taskViewModel.getSavedScrollIndex()
    val savedOffset = taskViewModel.getSavedScrollOffset()
    // Return to scroll position
    LaunchedEffect(selectedTasks) {
        if (selectedTasks.isNotEmpty() && savedIndex > 0) {
            listState.scrollToItem(savedIndex, savedOffset)
        }
    }
    // Save scroll position
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        taskViewModel.saveScrollPosition(
            listState.firstVisibleItemIndex,
            listState.firstVisibleItemScrollOffset
        )
    }

    var taskPendingDeletion by remember { mutableStateOf<Task?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
        ) {
        WeekNavigation(
            currentYearMonth = navYearMonth,
            weekNumber = navWeekDisplay.number,
            weekYear = navWeekDisplay.year,
            availableWeeks = navWeekNumbers,
            weekDays = navWeekDays,
            selectedDate = navSelectedDate,
            onPreviousWeek = { taskViewModel.goToPreviousWeek() },
            onNextWeek = { taskViewModel.goToNextWeek() },
            onDaySelected = taskViewModel::goToDay,
            onMonthSelected = taskViewModel::goToMonth,
            onYearSelected = taskViewModel::goToYear,
            onWeekSelected = taskViewModel::goToWeek
        )
        MainContentContainer {
            MainHeader(selectedDate.toDayHeader())

            LazyColumnContainer(state = listState) {
                if (selectedTasks.isNotEmpty()) {
                    items(
                        items = selectedTasks,
                        key = { task -> task.id },
                        contentType = { "task" }
                        ) { task ->
                        DayComponent(
                            task = task,
                            isExpanded = expandedTaskIds.contains(task.id),
                            onToggleExpanded = taskViewModel::toggleTaskExpanded,
                            onDelete = { taskPendingDeletion = it }
                        )
                    }
                } else {
                    item {
                        EmptyTask()
                    }
                }
            }
        }
    }

    DeleteTaskDialog(
        task = taskPendingDeletion,
        onDismiss = { taskPendingDeletion = null },
        onConfirm = {
            taskPendingDeletion?.let { taskViewModel.deleteTask(it) }
            taskPendingDeletion = null
        }
    )
}

@Composable
fun EmptyTask() {
    Text("No tasks for this day")
}

@Composable
fun AddButtonRow(taskViewModel: TaskViewModel) {
    val oneHour = remember { 60 * 60 * 1000L }
    val oneDay = remember { 24 * 60 * 60 * 1000L }
    val threeDays = remember { 3 * 24 * 60 * 60 * 1000L }
    val oneWeek = remember { 7 * 24 * 60 * 60 * 1000L }

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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
                    title = "Order Zweistra",
                    description = null,
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

@Composable
private fun DeleteTaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (task == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text("Are you sure you want to delete this task?")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = task.title,
                    style = TextStyles.Default.Bold.M
                )
                task.description?.let { description ->
                    Text(
                        text = description,
                        style = TextStyles.Default.M
                    )
                }
                Text(
                    text = task.date.toTaskTime(),
                    style = TextStyles.Grey.S
                )
            }
        }
    )
}
