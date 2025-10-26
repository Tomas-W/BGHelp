package com.example.bghelp.ui.screens.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.repository.TargetRepository
import com.example.bghelp.domain.model.CreateTarget
import com.example.bghelp.domain.model.Target
import com.example.bghelp.utils.getEpochRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class TargetViewModel @Inject constructor(
    private val targetRepository: TargetRepository
) : ViewModel() {
    private val _selectedDate = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val selectedDate: StateFlow<LocalDateTime> = _selectedDate.asStateFlow()

    private val _selectedWeek = MutableStateFlow<LocalDate>(LocalDate.now().with(DayOfWeek.MONDAY))
    val selectedWeek: StateFlow<LocalDate> = _selectedWeek.asStateFlow()

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

    fun addTarget(createTarget: CreateTarget) {
        viewModelScope.launch {
            targetRepository.addTarget(createTarget)
        }
    }

    fun deleteTarget(target: Target) {
        viewModelScope.launch {
            targetRepository.deleteTarget(target)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val targetsInRange: StateFlow<List<Target>> =
        _selectedDate
            .flatMapLatest { date ->
                val (start, end) = getEpochRange(date)
                targetRepository.getTargetByDateRange(start, end)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun updateSelectedWeek(date: LocalDate) {
        _selectedWeek.value = date
    }

    fun updateSelectedDate(date: LocalDateTime) {
        _selectedDate.value = date
    }

    val getAllTargets: StateFlow<List<Target>> =
        targetRepository.getAllTargets()
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

    fun isDaySelected(day: LocalDate): Boolean {
        return _selectedDate.value.toLocalDate() == day
    }

    fun isDayToday(day: LocalDate): Boolean {
        return LocalDate.now() == day
    }
}