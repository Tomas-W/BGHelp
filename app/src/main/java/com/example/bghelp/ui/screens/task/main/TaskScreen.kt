package com.example.bghelp.ui.screens.task.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.bghelp.ui.components.CollapsibleDateSelector
import com.example.bghelp.ui.components.LazyColumnContainer
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.MainHeader
import com.example.bghelp.ui.components.OptionsModal
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.theme.MainBlue
import com.example.bghelp.utils.toDayHeader

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
    // Date selector (week navigation / calendar)
    val rawSelectedDate by remember { derivedStateOf { selectedDate.toLocalDate() } }
    val navYearMonth by taskViewModel.currentYearMonth.collectAsState()
    val calendarMonth by taskViewModel.calendarMonth.collectAsState()
    val navWeekDisplay by taskViewModel.weekDisplay.collectAsState()
    val navWeekNumbers by taskViewModel.availableWeekNumbers.collectAsState()
    val navWeekDays by taskViewModel.weekDays.collectAsState()
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
    // Track deletion and editing
    var taskPendingDeletion by remember { mutableStateOf<Task?>(null) }
    var taskPendingEdit by remember { mutableStateOf<Task?>(null) }
    
    // WeekNav height
    val weekNavHeight by taskViewModel.weekNavHeight.collectAsState()
    var isFirstComposition by remember { mutableStateOf(true) }
    
    val animatedWeekNavHeight by animateDpAsState(
        targetValue = weekNavHeight,
        animationSpec = if (isFirstComposition && weekNavHeight > 0.dp) {
            // No animation on first composition
            tween(0)
        } else {
            // Animate when state changes
            tween(300)
        },
        label = "FAB Offset"
    )
    
    // Mark first composition as complete
    LaunchedEffect(Unit) {
        isFirstComposition = false
    }
    
    // Callback to update date selector height
    val onDateSelectorExpansionChanged: (Boolean, Dp) -> Unit = { _, height ->
        taskViewModel.updateWeekNavHeight(height)
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
                        itemsIndexed(
                            items = selectedTasks,
                            key = { _, task -> task.id },
                            contentType = { _, _ -> "task" }
                        ) { index, task ->
                            val isFirst = index == 0
                            val isLast = index == selectedTasks.size - 1
                            DayComponent(
                                task = task,
                                isExpanded = expandedTaskIds.contains(task.id),
                                onToggleExpanded = taskViewModel::toggleTaskExpanded,
                                onDelete = { taskPendingDeletion = it },
                                onLongPress = { taskPendingDeletion = it },
                                isFirst = isFirst,
                                isLast = isLast
                            )
                        }
                    } else {
                        item {
                            EmptyTask()
                        }
                    }
                }
            }

            // Collapsible date selector (week nav / calendar) at bottom
            CollapsibleDateSelector(
                modifier = Modifier.fillMaxWidth(),
                currentYearMonth = navYearMonth,
                calendarMonth = calendarMonth,
                weekNumber = navWeekDisplay.number,
                availableWeeks = navWeekNumbers,
                weekDays = navWeekDays,
                selectedDate = rawSelectedDate,
                onPreviousWeek = { taskViewModel.goToPreviousWeek() },
                onNextWeek = { taskViewModel.goToNextWeek() },
                onDaySelected = taskViewModel::goToDay,
                onMonthSelected = taskViewModel::goToMonth,
                onYearSelected = taskViewModel::goToYear,
                onWeekSelected = taskViewModel::goToWeek,
                onCalendarMonthChanged = taskViewModel::setCalendarMonth,
                onExpansionChanged = onDateSelectorExpansionChanged,
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
                    shape = CircleShape,
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
    OptionsModal(
        isVisible = taskPendingDeletion != null,
        onDismissRequest = { taskPendingDeletion = null },
        content = {
            taskPendingDeletion?.let { task ->
                TaskPreviewComponent(task = task)
            }
        },
        cancelLabel = "Delete",
        cancelOption = {
            taskPendingDeletion?.let { taskViewModel.deleteTask(it) }
            taskPendingDeletion = null
        },
        confirmLabel = "Edit",
        confirmOption = {
            taskPendingDeletion?.let { task ->
                taskPendingEdit = task
                taskPendingDeletion = null
            }
        }
    )
    
    // Navigate to edit task
    LaunchedEffect(taskPendingEdit) {
        taskPendingEdit?.let { task ->
            if (overlayNavController != null) {
                overlayNavController.navigate("${Screen.Tasks.Add.route}?taskId=${task.id}") {
                    launchSingleTop = true
                }
                taskPendingEdit = null
            }
        }
    }
}

@Composable
fun EmptyTask() {
    Text("No tasks for this day")
}
