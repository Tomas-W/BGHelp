package com.example.bghelp.ui.screens.task.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.Task
import com.example.bghelp.utils.TaskImageStorage
import com.example.bghelp.utils.getEpochRange
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _selectedDate = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val selectedDate: StateFlow<LocalDateTime> = _selectedDate.asStateFlow()

    private val _selectedWeek = MutableStateFlow<LocalDate>(LocalDate.now().with(DayOfWeek.MONDAY))
    val selectedWeek: StateFlow<LocalDate> = _selectedWeek.asStateFlow()

    private val _savedScrollIndex = MutableStateFlow(0)
    private val _savedScrollOffset = MutableStateFlow(0)

    private val _expandedTaskIds = MutableStateFlow<Set<Int>>(emptySet())
    val expandedTaskIds: StateFlow<Set<Int>> = _expandedTaskIds.asStateFlow()

    val monthYear: StateFlow<String> = selectedWeek
        .map { it.format(DateTimeFormatter.ofPattern("MMMM ''yy")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val weekNumber: StateFlow<String> = selectedWeek
        .map { "week ${it.get(WeekFields.ISO.weekOfYear())}" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )
    
    val weekDays: StateFlow<List<LocalDate>> = selectedWeek
        .map { weekStart ->
            (0..6).map { weekStart.plusDays(it.toLong()) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun goToPreviousWeek() {
        updateSelectedWeek(_selectedWeek.value.minusWeeks(1))
    }

    fun goToNextWeek() {
        updateSelectedWeek(_selectedWeek.value.plusWeeks(1))
    }

    fun goToDay(day: LocalDate) {
        updateSelectedDate(day.atStartOfDay())
    }

    fun updateSelectedWeek(week: LocalDate) {
        _selectedWeek.value = week
    }

    fun updateSelectedDate(date: LocalDateTime) {
        _selectedDate.value = date
    }

    fun saveScrollPosition(index: Int, offset: Int) {
        _savedScrollIndex.value = index
        _savedScrollOffset.value = offset
    }

    fun getSavedScrollIndex(): Int = _savedScrollIndex.value
    fun getSavedScrollOffset(): Int = _savedScrollOffset.value

    fun toggleTaskExpanded(taskId: Int) {
        _expandedTaskIds.value = if (_expandedTaskIds.value.contains(taskId)) {
            _expandedTaskIds.value - taskId
        } else {
            _expandedTaskIds.value + taskId
        }
    }

    fun addTask(createTask: CreateTask) {
        viewModelScope.launch {
            taskRepository.addTask(createTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            task.image?.uri?.let { relativePath ->
                withContext(Dispatchers.IO) {
                    runCatching {
                        TaskImageStorage.deleteImage(appContext, relativePath)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksInRange: StateFlow<List<Task>> =
        _selectedDate
            .flatMapLatest { date ->
                val (start, end) = getEpochRange(date)
                taskRepository.getTasksByDateRange(start, end)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val getAllTasks: StateFlow<List<Task>> =
        taskRepository.getAllTasks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
