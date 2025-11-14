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
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
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

    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val isoWeekFields = WeekFields.ISO

    val monthYear: StateFlow<String> = selectedWeek
        .map { weekStart ->
            weekStart.plusDays(3)
                .format(monthYearFormatter)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _selectedWeek.value.plusDays(3).format(monthYearFormatter)
        )

    val weekDisplay: StateFlow<WeekDisplay> = selectedWeek
        .map { weekStart ->
            val weekNumber = weekStart.get(isoWeekFields.weekOfWeekBasedYear())
            val weekYear = weekStart.get(isoWeekFields.weekBasedYear())
            WeekDisplay(weekNumber, weekYear)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _selectedWeek.value.let { weekStart ->
                val weekNumber = weekStart.get(isoWeekFields.weekOfWeekBasedYear())
                val weekYear = weekStart.get(isoWeekFields.weekBasedYear())
                WeekDisplay(weekNumber, weekYear)
            }
        )

    val availableWeekNumbers: StateFlow<List<Int>> = weekDisplay
        .map { display ->
            val lastWeekNumber = LocalDate.of(display.year, 12, 28)
                .get(isoWeekFields.weekOfWeekBasedYear())
            (1..lastWeekNumber).toList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = run {
                val initialYear = _selectedWeek.value.get(isoWeekFields.weekBasedYear())
                val lastWeekNumber = LocalDate.of(initialYear, 12, 28)
                    .get(isoWeekFields.weekOfWeekBasedYear())
                (1..lastWeekNumber).toList()
            }
        )
    
    val currentYearMonth: StateFlow<YearMonth> = selectedWeek
        .map { weekStart ->
            val referenceDay = weekStart.plusDays(3)
            YearMonth.of(referenceDay.year, referenceDay.month)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = YearMonth.of(_selectedWeek.value.year, _selectedWeek.value.month)
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

    fun goToMonth(yearMonth: YearMonth) {
        val firstDayOfMonth = yearMonth.atDay(1)
        val weekStart = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        updateSelectedWeek(weekStart)
    }

    fun goToMonth(month: Month) {
        val currentWeekYear = _selectedWeek.value.get(isoWeekFields.weekBasedYear())
        goToMonth(YearMonth.of(currentWeekYear, month))
    }

    fun goToYear(year: Int) {
        val currentWeekNumber = _selectedWeek.value.get(isoWeekFields.weekOfWeekBasedYear())
        val maxWeek = LocalDate.of(year, 12, 28).get(isoWeekFields.weekOfWeekBasedYear())
        val targetWeek = currentWeekNumber.coerceAtMost(maxWeek)
        goToWeek(targetWeek, year)
    }

    fun goToWeek(weekNumber: Int) {
        val currentWeekYear = _selectedWeek.value.get(isoWeekFields.weekBasedYear())
        goToWeek(weekNumber, currentWeekYear)
    }

    private fun goToWeek(weekNumber: Int, weekYear: Int) {
        val referenceDate = LocalDate.of(weekYear, 1, 4)
        val weekStart = referenceDate
            .with(isoWeekFields.weekOfWeekBasedYear(), weekNumber.toLong())
            .with(isoWeekFields.dayOfWeek(), DayOfWeek.MONDAY.value.toLong())
        updateSelectedWeek(weekStart)
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

data class WeekDisplay(
    val number: Int,
    val year: Int
)
