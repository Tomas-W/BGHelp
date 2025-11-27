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
import java.time.ZoneId

enum class DeletionType {
    SINGLE_OCCURRENCE,
    BASE_TASK_MARK_DELETED,
    DELETE_ALL,
    REGULAR_TASK
}

private enum class RecurringActionType {
    DELETE,
    EDIT
}

private enum class ModalState {
    INITIAL,          // Delete or Edit choice
    DELETE_RECURRING, // Delete This or All
    EDIT_RECURRING    // Edit This or All
}

private data class PendingDeletion(
    val task: Task,
    val deletionType: DeletionType
)

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
    // Track deletion and editing
    var taskPendingEdit by remember { mutableStateOf<Task?>(null) }
    var pendingDeletions by remember { mutableStateOf<Map<String, PendingDeletion>>(emptyMap()) }
    var modalTask by remember { mutableStateOf<Task?>(null) }
    var modalState by remember { mutableStateOf<ModalState>(ModalState.INITIAL) }
    var isRecurringModalBase by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    // Date selector (week navigation / calendar)
    val rawSelectedDate by remember { derivedStateOf { selectedDate.toLocalDate() } }
    val navYearMonth by taskViewModel.currentYearMonth.collectAsState()
    val calendarMonth by taskViewModel.calendarMonth.collectAsState()
    val navWeekDisplay by taskViewModel.weekDisplay.collectAsState()
    val navWeekNumbers by taskViewModel.availableWeekNumbers.collectAsState()
    val navWeekDays by taskViewModel.weekDays.collectAsState()
    val dateSelectorView by taskViewModel.dateSelectorView.collectAsState()
    // Scroll state
    val listState = rememberLazyListState()
    val savedIndex = taskViewModel.getSavedScrollIndex()
    val savedOffset = taskViewModel.getSavedScrollOffset()

    // Key for lazyColumn and recurrence identifying (delete/edit)
    fun getTaskKey(task: Task): String {
        val epoch = task.date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return "${task.id}_$epoch"
    }

    fun dismissModal() {
        modalTask = null
        modalState = ModalState.INITIAL
    }

    // Deletes after delay if not cancelled
    fun scheduleDeletion(pendingDeletion: PendingDeletion) {
        val taskKey = getTaskKey(pendingDeletion.task)
        coroutineScope.launch {
            delay(2500)
            // Check if deletion is still pending (not cancelled)
            if (pendingDeletions.containsKey(taskKey)) {
                when (pendingDeletion.deletionType) {
                    DeletionType.SINGLE_OCCURRENCE -> {
                        taskViewModel.deleteRecurringTaskOccurrence(pendingDeletion.task)
                    }
                    DeletionType.BASE_TASK_MARK_DELETED -> {
                        taskViewModel.markRecurringTaskBaseAsDeleted(pendingDeletion.task)
                    }
                    DeletionType.DELETE_ALL -> {
                        taskViewModel.deleteAllRecurringTaskOccurrences(pendingDeletion.task)
                    }
                    DeletionType.REGULAR_TASK -> {
                        taskViewModel.deleteTask(pendingDeletion.task)
                    }
                }
                pendingDeletions = pendingDeletions - taskKey
            }
        }
    }

    // Add to delayed deletion queue
    fun enqueueDeletion(task: Task, deletionType: DeletionType) {
        val taskKey = getTaskKey(task)
        val pendingDeletion = PendingDeletion(task, deletionType)
        pendingDeletions = pendingDeletions + (taskKey to pendingDeletion)
        scheduleDeletion(pendingDeletion)
    }

    // Delete or show recurring modal
    fun handleDeleteChoice() {
        val task = modalTask ?: return
        
        if (taskViewModel.isRecurringTask(task)) {
            modalState = ModalState.DELETE_RECURRING
        } else {
            enqueueDeletion(task, DeletionType.REGULAR_TASK)
            dismissModal()
        }
    }

    // Delete this or all occurrences
    fun handleRecurringDelete(deleteAll: Boolean) {
        val task = modalTask ?: return
        val deletionType = when {
            deleteAll -> DeletionType.DELETE_ALL
            isRecurringModalBase -> DeletionType.BASE_TASK_MARK_DELETED
            else -> DeletionType.SINGLE_OCCURRENCE
        }
        enqueueDeletion(task, deletionType)
        dismissModal()
    }

    // Edit or show recurring modal
    fun handleEditChoice() {
        val task = modalTask ?: return
        
        if (taskViewModel.isRecurringTask(task)) {
            modalState = ModalState.EDIT_RECURRING
        } else {
            taskPendingEdit = task
            dismissModal()
        }
    }

    // Edit this or all occurrences
    fun handleRecurringEdit(editAll: Boolean) {
        val task = modalTask ?: return
        taskPendingEdit = task
        dismissModal()
    }

    // WeekNav height
    val weekNavHeight by taskViewModel.weekNavHeight.collectAsState()
    var isFirstComposition by remember { mutableStateOf(true) }
    // Animated FAB offset
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
                            val isFirst = index == 0 // For rounded corners
                            val isLast = index == selectedTasks.size - 1 // For rounded corners
                            val taskKey = getTaskKey(task)
                            val isPendingDeletion = pendingDeletions.containsKey(taskKey) // For deletion overlay
                            DayComponent(
                                task = task,
                                isExpanded = expandedTaskIds.contains(task.id),
                                onToggleExpanded = taskViewModel::toggleTaskExpanded,
                                onLongPress = { pressedTask ->
                                    modalTask = pressedTask
                                    modalState = ModalState.INITIAL
                                },
                                isPendingDeletion = isPendingDeletion,
                                onCancelDeletion = {
                                    pendingDeletions = pendingDeletions - taskKey
                                },
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
                currentView = dateSelectorView,
                onViewChanged = taskViewModel::updateDateSelectorView
            )
        }

        // Add Task FAB - moves in sync with WeekNav
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

    // Track base occurrence info for recurring modal
    LaunchedEffect(modalTask) {
        modalTask?.let { task ->
            isRecurringModalBase = taskViewModel.isBaseRecurringTask(task)
        } ?: run {
            isRecurringModalBase = false
        }
    }

    // Single modal with different states (delete/edit | this/all)
    OptionsModal(
        isVisible = modalTask != null,
        onDismissRequest = { dismissModal() },
        dismissOnAction = false,
        title = when (modalState) {
            ModalState.INITIAL -> "Delete or edit?"
            ModalState.DELETE_RECURRING -> "This is a recurring Task.\nDelete this Task or all occurrences?"
            ModalState.EDIT_RECURRING -> "This is a recurring Task.\nEdit this Task or all occurrences?"
        },
        content = {
            modalTask?.let { task ->
                TaskPreviewComponent(task = task)
            }
        },
        secondLabel = when (modalState) {
            ModalState.INITIAL -> "Delete"
            ModalState.DELETE_RECURRING, ModalState.EDIT_RECURRING -> "This Task"
        },
        secondOnClick = {
            when (modalState) {
                ModalState.INITIAL -> handleDeleteChoice()
                ModalState.DELETE_RECURRING -> handleRecurringDelete(deleteAll = false)
                ModalState.EDIT_RECURRING -> handleRecurringEdit(editAll = false)
            }
        },
        firstLabel = when (modalState) {
            ModalState.INITIAL -> "Edit"
            ModalState.DELETE_RECURRING, ModalState.EDIT_RECURRING -> "All Tasks"
        },
        firstOnClick = {
            when (modalState) {
                ModalState.INITIAL -> handleEditChoice()
                ModalState.DELETE_RECURRING -> handleRecurringDelete(deleteAll = true)
                ModalState.EDIT_RECURRING -> handleRecurringEdit(editAll = true)
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
