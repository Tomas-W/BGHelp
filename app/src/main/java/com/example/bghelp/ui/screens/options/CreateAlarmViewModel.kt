package com.example.bghelp.ui.screens.options

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CreateAlarmViewModel @Inject constructor() : ViewModel() {
    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedTime = MutableStateFlow<LocalTime>(LocalTime.of(17, 30))
    val selectedTime: StateFlow<LocalTime> = _selectedTime.asStateFlow()

    fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun updateSelectedTime(time: LocalTime) {
        _selectedTime.value = time
    }
}

