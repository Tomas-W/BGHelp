package com.example.bghelp.ui.screens.task.add

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

class AddTaskFormStateHolder(
    private val onRepeatRuleChanged: () -> Unit
) {
    private val _formState = MutableStateFlow(AddTaskFormState.default())
    val formState: StateFlow<AddTaskFormState> = _formState.asStateFlow()

    // TITLE
    fun updateTitle(text: String) {
        _formState.update { it.copy(title = text) }
    }
    
    fun updateInfo(text: String) {
        _formState.update { it.copy(info = text) }
    }
    // TITLE

    // DATE
    fun toggleDateSelection() {
        _formState.update { it.copy(dateSelection = it.dateSelection.toggle()) }
    }
    
    fun setDateStart(date: LocalDate) {
        _formState.update { state ->
            val updatedState = state.copy(startDate = date)
            val endDate = if (updatedState.isEndDateVisible && updatedState.endDate != null) {
                if (date.isAfter(updatedState.endDate)) {
                    date
                } else {
                    updatedState.endDate
                }
            } else {
                updatedState.endDate
            }
            updatedState.copy(endDate = endDate)
        }
        onRepeatRuleChanged()
    }
    
    fun toggleEndDateVisible() {
        _formState.update { state ->
            if (state.isEndDateVisible) {
                state.copy(
                    isEndDateVisible = false,
                    endDate = null
                )
            } else {
                val initialEndDate = state.endDate ?: state.startDate
                state.copy(
                    isEndDateVisible = true,
                    endDate = initialEndDate.coerceAtLeast(state.startDate)
                )
            }
        }
        onRepeatRuleChanged()
    }
    
    fun setDateEnd(date: LocalDate) {
        _formState.update { state ->
            state.copy(
                endDate = date,
                startDate = state.startDate.coerceAtMost(date)
            )
        }
        onRepeatRuleChanged()
    }
    
    fun setTimeStart(time: LocalTime) {
        _formState.update { it.copy(startTime = time) }
    }
    
    fun toggleEndTimeVisible() {
        _formState.update { state ->
            if (state.isEndTimeVisible) {
                state.copy(
                    isEndTimeVisible = false,
                    endTime = null
                )
            } else {
                state.copy(
                    isEndTimeVisible = true,
                    endTime = state.endTime ?: LocalTime.of(13, 0)
                )
            }
        }
    }
    
    fun setTimeEnd(time: LocalTime) {
        _formState.update { it.copy(endTime = time) }
    }
    // DATE

    // REPEAT
    fun toggleRepeatSelection() {
        _formState.update { state ->
            state.copy(repeatSelection = state.repeatSelection.toggle())
        }
        onRepeatRuleChanged()
    }
    
    fun toggleWeeklySelectedDays(day: Int) {
        _formState.update { state ->
            val currentDays = state.weeklySelectedDays.toMutableSet()
            if (currentDays.contains(day)) {
                currentDays.remove(day)
            } else {
                currentDays.add(day)
            }
            state.copy(weeklySelectedDays = currentDays)
        }
        onRepeatRuleChanged()
    }
    
    fun setWeeklyIntervalWeeks(weeks: Int) {
        _formState.update { it.copy(weeklyIntervalWeeks = weeks) }
        onRepeatRuleChanged()
    }
    
    fun toggleMonthlySelectedMonths(month: Int) {
        _formState.update { state ->
            val currentMonths = state.monthlySelectedMonths.toMutableSet()
            if (currentMonths.contains(month)) {
                currentMonths.remove(month)
            } else {
                currentMonths.add(month)
            }
            state.copy(monthlySelectedMonths = currentMonths)
        }
        onRepeatRuleChanged()
    }
    
    fun toggleMonthSelectedDays(day: Int) {
        _formState.update { state ->
            val currentDays = state.monthlySelectedDays.toMutableSet()
            if (currentDays.contains(day)) {
                currentDays.remove(day)
            } else {
                currentDays.add(day)
            }
            state.copy(monthlySelectedDays = currentDays)
        }
        onRepeatRuleChanged()
    }
    
    fun selectAllMonthlySelectedMonths() {
        _formState.update { it.copy(monthlySelectedMonths = (1..12).toSet()) }
        onRepeatRuleChanged()
    }
    
    fun deselectAllMonthlySelectedMonths() {
        _formState.update { it.copy(monthlySelectedMonths = emptySet()) }
        onRepeatRuleChanged()
    }
    
    fun toggleMonthlyDaySelection(newSelection: RepeatMonthlyDaySelection) {
        _formState.update { it.copy(monthlyDaySelection = newSelection) }
        onRepeatRuleChanged()
    }
    
    fun selectAllMonthlySelectedDays() {
        _formState.update { it.copy(monthlySelectedDays = (1..31).toSet()) }
        onRepeatRuleChanged()
    }
    
    fun deselectAllMonthlySelectedDays() {
        _formState.update { it.copy(monthlySelectedDays = emptySet()) }
        onRepeatRuleChanged()
    }
    // REPEAT

    // RRULE
    fun updateRepeatRRule(rrule: String?) {
        _formState.update { it.copy(repeatRRule = rrule) }
    }
    // RRULE

    // REMIND
    fun toggleRemindSelection() {
        _formState.update { state ->
            state.copy(remindSelection = state.remindSelection.toggle())
        }
    }
    
    fun addReminder(type: RemindType, nextId: Int): Int {
        val newReminder = Reminder(
            id = nextId,
            value = AddTaskConstants.REMINDER_START,
            timeUnit = TimeUnit.MINUTES
        )
        _formState.update { state ->
            when (type) {
                RemindType.START -> state.copy(startReminders = state.startReminders + newReminder)
                RemindType.END -> state.copy(endReminders = state.endReminders + newReminder)
            }
        }
        return nextId + 1
    }
    
    fun removeReminder(type: RemindType, reminderId: Int) {
        _formState.update { state ->
            when (type) {
                RemindType.START -> state.copy(
                    startReminders = state.startReminders.filter { it.id != reminderId }
                )
                RemindType.END -> state.copy(
                    endReminders = state.endReminders.filter { it.id != reminderId }
                )
            }
        }
    }
    
    fun updateReminder(reminderType: RemindType, reminderId: Int, value: Int? = null, timeUnit: TimeUnit? = null) {
        val validatedValue = value?.coerceIn(AddTaskConstants.MIN_REMINDER, AddTaskConstants.MAX_REMINDER)
        
        _formState.update { state ->
            when (reminderType) {
                RemindType.START -> state.copy(
                    startReminders = state.startReminders.map { reminder ->
                        if (reminder.id == reminderId) {
                            reminder.copy(
                                value = validatedValue ?: reminder.value,
                                timeUnit = timeUnit ?: reminder.timeUnit
                            )
                        } else {
                            reminder
                        }
                    }
                )
                RemindType.END -> state.copy(
                    endReminders = state.endReminders.map { reminder ->
                        if (reminder.id == reminderId) {
                            reminder.copy(
                                value = validatedValue ?: reminder.value,
                                timeUnit = timeUnit ?: reminder.timeUnit
                            )
                        } else {
                            reminder
                        }
                    }
                )
            }
        }
    }
    // REMIND

    // SOUND
    fun toggleSoundSelection() {
        _formState.update { state ->
            state.copy(soundSelection = state.soundSelection.toggle())
        }
    }
    
    fun setSelectedAudioFile(fileName: String) {
        _formState.update { it.copy(selectedAudioFile = fileName) }
    }
    // SOUND

    // VIBRATE
    fun toggleVibrateSelection() {
        _formState.update { state ->
            state.copy(vibrateSelection = state.vibrateSelection.toggle())
        }
    }
    // VIBRATE

    // NOTE
    fun toggleNoteSelection() {
        _formState.update { state ->
            state.copy(noteSelection = state.noteSelection.toggle())
        }
    }

    fun updateNote(text: String) {
        _formState.update { it.copy(note = text) }
    }
    // NOTE

    // LOCATION
    fun toggleLocationSelection() {
        _formState.update { state ->
            state.copy(locationSelection = state.locationSelection.toggle())
        }
    }
    
    fun setSelectedLocations(locations: List<TaskLocation>) {
        _formState.update { state ->
            state.copy(
                selectedLocations = locations,
                locationSelection = if (locations.isEmpty()) UserLocationSelection.OFF else UserLocationSelection.ON
            )
        }
    }
    
    fun appendSelectedLocations(locations: List<TaskLocation>) {
        if (locations.isEmpty()) return
        _formState.update { state ->
            val merged = (state.selectedLocations + locations).distinctBy { location ->
                location.latitude to location.longitude
            }
            state.copy(
                selectedLocations = merged,
                locationSelection = if (merged.isEmpty()) UserLocationSelection.OFF else UserLocationSelection.ON
            )
        }
    }
    
    fun removeSelectedLocation(location: TaskLocation) {
        _formState.update { state ->
            val updatedLocations = state.selectedLocations.filterNot { it == location }
            state.copy(
                selectedLocations = updatedLocations,
                locationSelection = if (updatedLocations.isEmpty()) UserLocationSelection.OFF else UserLocationSelection.ON
            )
        }
    }
    // LOCATION

    // IMAGE
    fun toggleImageSelection() {
        _formState.update { state ->
            state.copy(imageSelection = state.imageSelection.toggle())
        }
    }
    
    fun setImageFromGallery(uri: android.net.Uri, displayName: String?) {
        val label = displayName?.takeIf { it.isNotBlank() } ?: AddTaskStrings.NO_IMAGE_SELECTED
        _formState.update { state ->
            state.copy(
                selectedImage = TaskImageData(
                    displayName = label,
                    uri = uri,
                    bitmap = null, // Use URI not bitmap
                    source = TaskImageSource.GALLERY
                )
            )
        }
    }

    fun setImageFromCamera(uri: android.net.Uri, displayName: String) {
        _formState.update { state ->
            state.copy(
                selectedImage = TaskImageData(
                    displayName = displayName,
                    uri = uri,
                    bitmap = null, // Use URI not bitmap
                    source = TaskImageSource.CAMERA
                )
            )
        }
    }

    fun clearImage() {
        _formState.update { state ->
            state.copy(selectedImage = null)
        }
    }
    // IMAGE

    // COLOR
    fun toggleColorSelection() {
        _formState.update { state ->
            state.copy(colorSelection = state.colorSelection.toggle())
        }
    }
    
    fun setSelectedColorId(colorId: Int) {
        _formState.update { it.copy(selectedColorId = colorId) }
    }
    // COLOR

    // MISC
    fun reset() {
        _formState.value = AddTaskFormState.default()
    }
}

