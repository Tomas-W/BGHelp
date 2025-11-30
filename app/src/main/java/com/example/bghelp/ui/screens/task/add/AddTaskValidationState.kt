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

    val isTitleValid: StateFlow<Boolean> = formState
        .map { state -> AddTaskValidator.validateTitle(state.title, context) == null }
        .stateIn(
            scope = scope,
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
            scope = scope,
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
            scope = scope,
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
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isSoundValid: StateFlow<Boolean> = formState
        .map { state ->
            AddTaskValidator.validateSound(
                soundSelection = state.soundSelection,
                selectedAudioFile = state.selectedAudioFile,
                context = context
            ) == null
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isNoteValid: StateFlow<Boolean> = formState
        .map { state ->
            AddTaskValidator.validateNote(
                noteSelection = state.noteSelection,
                note = state.note,
                context = context
            ) == null
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isLocationValid: StateFlow<Boolean> = formState
        .map { state ->
            AddTaskValidator.validateLocation(
                locationSelection = state.locationSelection,
                selectedLocations = state.selectedLocations,
                context = context
            ) == null
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isImageValid: StateFlow<Boolean> = formState
        .map { state ->
            AddTaskValidator.validateImage(
                imageSelection = state.imageSelection,
                selectedImage = state.selectedImage,
                context = context
            ) == null
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isColorValid: StateFlow<Boolean> = formState
        .map { state ->
            AddTaskValidator.validateColor(
                colorSelection = state.colorSelection,
                selectedColorId = state.selectedColorId,
                context = context
            ) == null
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private val firstGroupValid: StateFlow<Boolean> = combine(
        isTitleValid,
        isDateTimeValid,
        isRepeatValid,
        isRemindersValid,
        isSoundValid
    ) { titleValid, dateTimeValid, repeatValid, remindersValid, soundValid ->
        titleValid && dateTimeValid && repeatValid && remindersValid && soundValid
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val secondGroupValid: StateFlow<Boolean> = combine(
        isNoteValid,
        isLocationValid,
        isImageValid,
        isColorValid
    ) { noteValid, locationValid, imageValid, colorValid ->
        noteValid && locationValid && imageValid && colorValid
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val isFormValid: StateFlow<Boolean> = combine(
        firstGroupValid,
        secondGroupValid
    ) { firstGroup, secondGroup ->
        firstGroup && secondGroup
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun validateForm(state: AddTaskFormState): String? {
        return validateTitle(state) 
            ?: validateDateTime(state)
            ?: validateRepeat(state)
            ?: validateReminders(state)
            ?: validateSound(state)
            ?: validateNote(state)
            ?: validateLocation(state)
            ?: validateImage(state)
            ?: validateColor(state)
    }

    private fun validateTitle(state: AddTaskFormState): String? =
        AddTaskValidator.validateTitle(state.title, context)

    private fun validateDateTime(state: AddTaskFormState): String? =
        AddTaskValidator.validateDateTime(
            dateSelection = state.dateSelection,
            startDate = state.startDate,
            startTime = state.startTime,
            endDate = state.endDate,
            isEndDateVisible = state.isEndDateVisible,
            endTime = state.endTime,
            isEndTimeVisible = state.isEndTimeVisible,
            context = context
        )

    private fun validateRepeat(state: AddTaskFormState): String? =
        AddTaskValidator.validateRepeat(
            repeatSelection = state.repeatSelection,
            weeklyDays = state.weeklySelectedDays,
            monthlyMonths = state.monthlySelectedMonths,
            monthlyDaySelection = state.monthlyDaySelection,
            monthlyDays = state.monthlySelectedDays,
            context = context
        )

    private fun validateReminders(state: AddTaskFormState): String? =
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
        )

    private fun validateSound(state: AddTaskFormState): String? =
        AddTaskValidator.validateSound(
            soundSelection = state.soundSelection,
            selectedAudioFile = state.selectedAudioFile,
            context = context
        )

    private fun validateNote(state: AddTaskFormState): String? =
        AddTaskValidator.validateNote(
            noteSelection = state.noteSelection,
            note = state.note,
            context = context
        )

    private fun validateLocation(state: AddTaskFormState): String? =
        AddTaskValidator.validateLocation(
            locationSelection = state.locationSelection,
            selectedLocations = state.selectedLocations,
            context = context
        )

    private fun validateImage(state: AddTaskFormState): String? =
        AddTaskValidator.validateImage(
            imageSelection = state.imageSelection,
            selectedImage = state.selectedImage,
            context = context
        )

    private fun validateColor(state: AddTaskFormState): String? =
        AddTaskValidator.validateColor(
            colorSelection = state.colorSelection,
            selectedColorId = state.selectedColorId,
            context = context
        )
}

