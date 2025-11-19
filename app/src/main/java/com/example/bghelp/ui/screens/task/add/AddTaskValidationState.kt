package com.example.bghelp.ui.screens.task.add

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class AddTaskValidationState(
    formState: StateFlow<AddTaskFormState>,
    scope: CoroutineScope
) {
    private val viewModelScope = scope

    val isTitleValid: StateFlow<Boolean> = formState
        .map { state -> AddTaskValidator.validateTitle(state.title) == null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    val isDateTimeValid: StateFlow<Boolean> = formState
        .map { state ->
            AddTaskValidator.validateDateTime(
                dateSelection = state.dateSelection,
                startDate = state.startDate,
                startTime = state.startTime,
                endDate = state.endDate,
                isEndDateVisible = state.isEndDateVisible,
                endTime = state.endTime,
                isEndTimeVisible = state.isEndTimeVisible
            ) == null
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    val isRepeatValid: StateFlow<Boolean> = formState
        .map { state ->
            AddTaskValidator.validateRepeat(
                repeatSelection = state.repeatSelection,
                weeklyDays = state.weeklySelectedDays,
                monthlyDaySelection = state.monthlyDaySelection,
                monthlyDays = state.monthlySelectedDays
            ) == null
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val isRemindersValid: StateFlow<Boolean> = formState
        .map { state ->
            AddTaskValidator.validateReminders(
                remindSelection = state.remindSelection,
                dateSelection = state.dateSelection,
                startDate = state.startDate,
                startTime = state.startTime,
                startReminders = state.startReminders,
                endReminders = state.endReminders,
                endDate = state.endDate,
                isEndDateVisible = state.isEndDateVisible,
                endTime = state.endTime,
                isEndTimeVisible = state.isEndTimeVisible
            ) == null
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
    
    val isFormValid: StateFlow<Boolean> = combine(
        isTitleValid,
        isDateTimeValid,
        isRepeatValid,
        isRemindersValid
    ) { titleValid, dateTimeValid, repeatValid, remindersValid ->
        titleValid && dateTimeValid && repeatValid && remindersValid
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    fun validateForm(state: AddTaskFormState): String? {
        val titleError = AddTaskValidator.validateTitle(state.title)
        if (titleError != null) return titleError
        
        val dateTimeError = AddTaskValidator.validateDateTime(
            dateSelection = state.dateSelection,
            startDate = state.startDate,
            startTime = state.startTime,
            endDate = state.endDate,
            isEndDateVisible = state.isEndDateVisible,
            endTime = state.endTime,
            isEndTimeVisible = state.isEndTimeVisible
        )
        if (dateTimeError != null) return dateTimeError
        
        val repeatError = AddTaskValidator.validateRepeat(
            repeatSelection = state.repeatSelection,
            weeklyDays = state.weeklySelectedDays,
            monthlyDaySelection = state.monthlyDaySelection,
            monthlyDays = state.monthlySelectedDays
        )
        if (repeatError != null) return repeatError
        
        val reminderError = AddTaskValidator.validateReminders(
            remindSelection = state.remindSelection,
            dateSelection = state.dateSelection,
            startDate = state.startDate,
            startTime = state.startTime,
            startReminders = state.startReminders,
            endReminders = state.endReminders,
            endDate = state.endDate,
            isEndDateVisible = state.isEndDateVisible,
            endTime = state.endTime,
            isEndTimeVisible = state.isEndTimeVisible
        )
        if (reminderError != null) return reminderError
        
        return null
    }
}

