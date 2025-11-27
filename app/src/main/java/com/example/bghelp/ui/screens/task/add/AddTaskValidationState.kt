package com.example.bghelp.ui.screens.task.add

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AddTaskValidationState(
    formState: StateFlow<AddTaskFormState>,
    scope: CoroutineScope,
    private val context: Context
) {
    private val viewModelScope = scope

    val isTitleValid: StateFlow<Boolean> = formState
        .map { state -> AddTaskValidator.validateTitle(state.title, context) == null }
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
                isEndTimeVisible = state.isEndTimeVisible,
                context = context
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
                monthlyMonths = state.monthlySelectedMonths,
                monthlyDaySelection = state.monthlyDaySelection,
                monthlyDays = state.monthlySelectedDays,
                context = context
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
                isEndTimeVisible = state.isEndTimeVisible,
                context = context
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
        val titleError = AddTaskValidator.validateTitle(state.title, context)
        if (titleError != null) return titleError

        val dateTimeError = AddTaskValidator.validateDateTime(
            dateSelection = state.dateSelection,
            startDate = state.startDate,
            startTime = state.startTime,
            endDate = state.endDate,
            isEndDateVisible = state.isEndDateVisible,
            endTime = state.endTime,
            isEndTimeVisible = state.isEndTimeVisible,
            context = context
        )
        if (dateTimeError != null) return dateTimeError

        val repeatError = AddTaskValidator.validateRepeat(
            repeatSelection = state.repeatSelection,
            weeklyDays = state.weeklySelectedDays,
            monthlyMonths = state.monthlySelectedMonths,
            monthlyDaySelection = state.monthlyDaySelection,
            monthlyDays = state.monthlySelectedDays,
            context = context
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
            isEndTimeVisible = state.isEndTimeVisible,
            context = context
        )
        if (reminderError != null) return reminderError

        return null
    }
}

