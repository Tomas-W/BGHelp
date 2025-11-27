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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
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
import com.example.bghelp.utils.toDayHeader
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneId

enum class DeletionType {
    SINGLE_OCCURRENCE,
    BASE_TASK_MARK_DELETED,
    DELETE_ALL,
    REGULAR_TASK
}

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
    var taskPendingDeletionWithTimer by remember { mutableStateOf<Pair<Int, Long>?>(null) }
    var taskPendingDeletionData by remember { mutableStateOf<Task?>(null) }
    var deletionType by remember { mutableStateOf<DeletionType?>(null) }
    var isTaskRecurring by remember { mutableStateOf(false) }
    var isTaskBase by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Cancel deletion function
    val cancelDeletion: () -> Unit = {
        taskPendingDeletionWithTimer = null
        taskPendingDeletionData = null
        deletionType = null
    }

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
                            key = { _, task -> 
                                val epoch = task.date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                "${task.id}_$epoch"
                            },
                            contentType = { _, _ -> "task" }
                        ) { index, task ->
                            val isFirst = index == 0
                            val isLast = index == selectedTasks.size - 1
                            val isPendingDeletion = taskPendingDeletionWithTimer?.let { (pendingId, pendingEpoch) ->
                                val taskEpoch = task.date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                task.id == pendingId && taskEpoch == pendingEpoch
                            } ?: false
                            DayComponent(
                                task = task,
                                isExpanded = expandedTaskIds.contains(task.id),
                                onToggleExpanded = taskViewModel::toggleTaskExpanded,
                                onLongPress = { taskPendingDeletion = it },
                                isPendingDeletion = isPendingDeletion,
                                onCancelDeletion = cancelDeletion,
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
                    containerColor = MaterialTheme.colorScheme.primary,
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

    // Check if task is recurring when modal opens
    LaunchedEffect(taskPendingDeletion) {
        taskPendingDeletion?.let { task ->
            isTaskRecurring = taskViewModel.isRecurringTask(task)
            if (isTaskRecurring) {
                isTaskBase = taskViewModel.isBaseRecurringTask(task)
            } else {
                isTaskBase = false
            }
        } ?: run {
            isTaskRecurring = false
            isTaskBase = false
        }
    }

    // Delete Task modal
    OptionsModal(
        isVisible = taskPendingDeletion != null,
        onDismissRequest = { taskPendingDeletion = null },
        title = if (isTaskRecurring) "This is a recurring Task.\nDelete only this Task or all occurrences?" else null,
        content = {
            taskPendingDeletion?.let { task ->
                TaskPreviewComponent(task = task)
            }
        },
        cancelLabel = "Delete",
        cancelOption = {
            taskPendingDeletion?.let { task ->
                coroutineScope.launch {
                    val isRecurring = taskViewModel.isRecurringTask(task)
                    if (isRecurring) {
                        val isBase = taskViewModel.isBaseRecurringTask(task)
                        deletionType = if (isBase) {
                            DeletionType.BASE_TASK_MARK_DELETED
                        } else {
                            DeletionType.SINGLE_OCCURRENCE
                        }
                    } else {
                        deletionType = DeletionType.REGULAR_TASK
                    }
                    val taskEpoch = task.date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    taskPendingDeletionWithTimer = Pair(task.id, taskEpoch)
                    taskPendingDeletionData = task
                    taskPendingDeletion = null
                }
            }
        },
        confirmLabel = "Edit",
        confirmOption = {
            taskPendingDeletion?.let { task ->
                taskPendingEdit = task
                taskPendingDeletion = null
            }
        },
        extraLabel = if (isTaskRecurring) "Delete All" else null,
        extraOption = if (isTaskRecurring) {
            {
                taskPendingDeletion?.let { task ->
                    deletionType = DeletionType.DELETE_ALL
                    val taskEpoch = task.date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    taskPendingDeletionWithTimer = Pair(task.id, taskEpoch)
                    taskPendingDeletionData = task
                    taskPendingDeletion = null
                }
            }
        } else null
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

    // Handle deletion timer
    LaunchedEffect(taskPendingDeletionWithTimer, deletionType) {
        val timerData = taskPendingDeletionWithTimer
        val taskData = taskPendingDeletionData
        val typeToDelete = deletionType
        if (timerData != null && taskData != null && typeToDelete != null) {
            val (taskId, taskEpoch) = timerData
            delay(2500)
            if (taskPendingDeletionWithTimer?.first == taskId && 
                taskPendingDeletionWithTimer?.second == taskEpoch && 
                deletionType == typeToDelete) {
                coroutineScope.launch {
                    when (typeToDelete) {
                        DeletionType.SINGLE_OCCURRENCE -> {
                            taskViewModel.deleteRecurringTaskOccurrence(taskData)
                        }
                        DeletionType.BASE_TASK_MARK_DELETED -> {
                            taskViewModel.markRecurringTaskBaseAsDeleted(taskData)
                        }
                        DeletionType.DELETE_ALL -> {
                            taskViewModel.deleteAllRecurringTaskOccurrences(taskData)
                        }
                        DeletionType.REGULAR_TASK -> {
                            taskViewModel.deleteTask(taskData)
                        }
                    }
                }
                taskPendingDeletionWithTimer = null
                taskPendingDeletionData = null
                deletionType = null
            }
        }
    }
}

@Composable
fun EmptyTask() {
    Text("No tasks for this day")
}
