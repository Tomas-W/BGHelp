package com.example.bghelp.ui.screens.task.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bghelp.domain.model.Task
import com.example.bghelp.ui.components.CollapsibleWeekNavigation
import com.example.bghelp.ui.components.LazyColumnContainer
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.MainHeader
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.theme.MainBlue
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.utils.toDayHeader
import com.example.bghelp.utils.toTaskTime

@SuppressLint("FrequentlyChangingValue", "FrequentlyChangedStateReadInComposition")
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    overlayNavController: NavHostController? = null
) {
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
    // Track deletion
    var taskPendingDeletion by remember { mutableStateOf<Task?>(null) }
    
    // Track week nav height for FAB positioning
    var weekNavHeight by remember { mutableStateOf(0.dp) }
    val animatedWeekNavHeight by animateDpAsState(
        targetValue = weekNavHeight,
        animationSpec = tween(300),
        label = "WeekNav Height"
    )
    // Callback to track week nav height
    val onWeekNavExpansionChanged: (Boolean, Dp) -> Unit = { _, height ->
        weekNavHeight = height
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Takes remaining space (depending on WeekNav)
            MainContentContainer(
                modifier = Modifier.weight(1f)
            ) {
                MainHeader(selectedDate.toDayHeader())

                LazyColumnContainer(
                    state = listState
                ) {
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

            // Collapsible week navigation at bottom
            CollapsibleWeekNavigation(
                currentYearMonth = navYearMonth,
                weekNumber = navWeekDisplay.number,
                availableWeeks = navWeekNumbers,
                weekDays = navWeekDays,
                selectedDate = navSelectedDate,
                onPreviousWeek = { taskViewModel.goToPreviousWeek() },
                onNextWeek = { taskViewModel.goToNextWeek() },
                onDaySelected = taskViewModel::goToDay,
                onMonthSelected = taskViewModel::goToMonth,
                onYearSelected = taskViewModel::goToYear,
                onWeekSelected = taskViewModel::goToWeek,
                onExpansionChanged = onWeekNavExpansionChanged,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // FAB above WeekNav - moves in sync
        if (overlayNavController != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        overlayNavController.navigate(Screen.Tasks.Add.route) {
                            launchSingleTop = true
                        }
                    },
                    containerColor = MainBlue,
                    modifier = Modifier.offset(y = -animatedWeekNavHeight)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add task"
                    )
                }
            }
        }
    }

    // Delete Task modal
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
