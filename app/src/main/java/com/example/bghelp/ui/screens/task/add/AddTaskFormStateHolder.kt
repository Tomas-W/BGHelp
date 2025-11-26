package com.example.bghelp.ui.screens.task.add

import android.net.Uri
import androidx.core.net.toUri
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.ReminderOffsetUnit
import com.example.bghelp.domain.model.Task
import com.example.bghelp.domain.model.TaskImageSourceOption
import com.example.bghelp.domain.service.RecurrenceCalculator
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

    fun toggleEndDateVisible(restoreDate: LocalDate? = null) {
        _formState.update { state ->
            if (state.isEndDateVisible) {
                state.copy(
                    isEndDateVisible = false,
                    endDate = null
                )
            } else {
                val initialEndDate = restoreDate ?: state.endDate ?: state.startDate
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

    fun toggleEndTimeVisible(restoreTime: LocalTime? = null) {
        _formState.update { state ->
            if (state.isEndTimeVisible) {
                state.copy(
                    isEndTimeVisible = false,
                    endTime = null
                )
            } else {
                state.copy(
                    isEndTimeVisible = true,
                    endTime = restoreTime ?: state.endTime ?: LocalTime.of(13, 0)
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

    fun updateReminder(
        reminderType: RemindType,
        reminderId: Int,
        value: Int? = null,
        timeUnit: TimeUnit? = null
    ) {
        val validatedValue =
            value?.coerceIn(AddTaskConstants.MIN_REMINDER, AddTaskConstants.MAX_REMINDER)

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

    fun updateSnooze(snoozeIndex: Int, value: Int? = null, timeUnit: TimeUnit? = null) {
        val validatedValue =
            value?.coerceIn(AddTaskConstants.MIN_SNOOZE, AddTaskConstants.MAX_SNOOZE)

        _formState.update { state ->
            when (snoozeIndex) {
                1 -> state.copy(
                    snoozeValue1 = validatedValue ?: state.snoozeValue1,
                    snoozeUnit1 = timeUnit ?: state.snoozeUnit1
                )

                2 -> state.copy(
                    snoozeValue2 = validatedValue ?: state.snoozeValue2,
                    snoozeUnit2 = timeUnit ?: state.snoozeUnit2
                )

                else -> state
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

    fun setImageFromGallery(uri: Uri, displayName: String?) {
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

    fun setImageFromCamera(uri: Uri, displayName: String) {
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

    fun restore(state: AddTaskFormState) {
        _formState.value = state
    }

    fun loadFromTask(task: Task) {
        val date = task.date.toLocalDate()
        val time = task.date.toLocalTime()
        val endDate = task.endDate?.toLocalDate()
        val endTime = task.endDate?.toLocalTime()

        val dateSelection = if (task.allDay) UserDateSelection.ON else UserDateSelection.OFF
        val isEndDateVisible = endDate != null
        val isEndTimeVisible = endTime != null

        val repeatSelection = parseRepeatSelection(task.rrule)
        val (weeklyDays, weeklyInterval) = parseWeeklyRepeat(task.rrule)
        val (monthlyMonths, monthlyDaySelection, monthlyDays) = parseMonthlyRepeat(task.rrule)

        val remindSelection =
            if (task.reminders.isNotEmpty()) UserRemindSelection.ON else UserRemindSelection.OFF
        val startReminders = task.reminders
            .filter { it.type == ReminderKind.START }
            .mapIndexed { index, entry ->
                Reminder(
                    id = index,
                    value = entry.offsetValue,
                    timeUnit = entry.offsetUnit.toTimeUnit()
                )
            }
        val endReminders = task.reminders
            .filter { it.type == ReminderKind.END }
            .mapIndexed { index, entry ->
                Reminder(
                    id = index,
                    value = entry.offsetValue,
                    timeUnit = entry.offsetUnit.toTimeUnit()
                )
            }

        val soundSelection = task.sound.toUserSoundSelection()
        val vibrateSelection = task.vibrate.toUserVibrateSelection()

        val noteSelection =
            if (task.note != null && task.note.isNotBlank()) UserNoteSelection.ON else UserNoteSelection.OFF

        val locationSelection =
            if (task.locations.isNotEmpty()) UserLocationSelection.ON else UserLocationSelection.OFF
        val selectedLocations = task.locations.map { entry ->
            TaskLocation(
                latitude = entry.latitude,
                longitude = entry.longitude,
                address = entry.address,
                name = entry.name
            )
        }

        val imageSelection =
            if (task.image != null) UserImageSelection.ON else UserImageSelection.OFF
        val selectedImage = task.image?.let { image ->
            TaskImageData(
                displayName = image.displayName ?: AddTaskStrings.NO_IMAGE_SELECTED,
                uri = image.uri?.toUri(),
                bitmap = null,
                source = when (image.source) {
                    TaskImageSourceOption.GALLERY -> TaskImageSource.GALLERY
                    TaskImageSourceOption.CAMERA -> TaskImageSource.CAMERA
                }
            )
        }

        val colorSelection =
            if (task.color.id != 1) UserColorSelection.ON else UserColorSelection.OFF

        _formState.value = AddTaskFormState(
            title = task.title,
            info = task.description ?: "",
            dateSelection = dateSelection,
            startDate = date,
            endDate = endDate,
            isEndDateVisible = isEndDateVisible,
            startTime = time,
            endTime = endTime,
            isEndTimeVisible = isEndTimeVisible,
            repeatSelection = repeatSelection,
            weeklySelectedDays = weeklyDays,
            weeklyIntervalWeeks = weeklyInterval,
            monthlySelectedMonths = monthlyMonths,
            monthlyDaySelection = monthlyDaySelection,
            monthlySelectedDays = monthlyDays,
            repeatRRule = task.rrule,
            remindSelection = remindSelection,
            startReminders = startReminders,
            endReminders = endReminders,
            soundSelection = soundSelection,
            selectedAudioFile = task.soundUri ?: "",
            vibrateSelection = vibrateSelection,
            snoozeValue1 = task.snoozeValue1,
            snoozeUnit1 = task.snoozeUnit1.toTimeUnit(),
            snoozeValue2 = task.snoozeValue2,
            snoozeUnit2 = task.snoozeUnit2.toTimeUnit(),
            noteSelection = noteSelection,
            note = task.note ?: "",
            locationSelection = locationSelection,
            selectedLocations = selectedLocations,
            imageSelection = imageSelection,
            selectedImage = selectedImage,
            colorSelection = colorSelection,
            selectedColorId = task.color.id
        )
        onRepeatRuleChanged()
    }

    private fun parseRepeatSelection(rrule: String?): UserRepeatSelection {
        if (rrule.isNullOrBlank()) return UserRepeatSelection.OFF
        val rule = RecurrenceCalculator.parseRRule(rrule) ?: return UserRepeatSelection.OFF
        return when (rule.frequency) {
            RecurrenceCalculator.Frequency.DAILY -> UserRepeatSelection.OFF
            RecurrenceCalculator.Frequency.WEEKLY -> UserRepeatSelection.WEEKLY
            RecurrenceCalculator.Frequency.MONTHLY -> UserRepeatSelection.MONTHLY
        }
    }

    private fun parseWeeklyRepeat(rrule: String?): Pair<Set<Int>, Int> {
        if (rrule.isNullOrBlank()) return setOf(1, 2, 3, 4, 5, 6, 7) to 1
        val rule = RecurrenceCalculator.parseRRule(rrule) ?: return setOf(1, 2, 3, 4, 5, 6, 7) to 1
        if (rule.frequency != RecurrenceCalculator.Frequency.WEEKLY) return setOf(
            1, 2, 3, 4, 5, 6, 7) to 1

        val days = rule.byDay.map { it.value }.toSet()
        return (days.ifEmpty { setOf(1, 2, 3, 4, 5, 6, 7) }) to rule.interval
    }

    private fun parseMonthlyRepeat(rrule: String?): Triple<Set<Int>, RepeatMonthlyDaySelection, Set<Int>> {
        if (rrule.isNullOrBlank()) return Triple(
            (1..12).toSet(),
            RepeatMonthlyDaySelection.ALL,
            (1..31).toSet()
        )
        val rule = RecurrenceCalculator.parseRRule(rrule) ?: return Triple(
            (1..12).toSet(),
            RepeatMonthlyDaySelection.ALL,
            (1..31).toSet()
        )
        if (rule.frequency != RecurrenceCalculator.Frequency.MONTHLY) return Triple(
            (1..12).toSet(),
            RepeatMonthlyDaySelection.ALL,
            (1..31).toSet()
        )

        val months = rule.byMonth.ifEmpty { (1..12).toSet() }
        val days = rule.byMonthDay.toSet()
        val daySelection = when {
            days.contains(-1) -> RepeatMonthlyDaySelection.LAST
            days.isEmpty() || days.containsAll((1..31).toSet()) -> RepeatMonthlyDaySelection.ALL
            else -> RepeatMonthlyDaySelection.SELECT
        }
        val selectedDays =
            if (daySelection == RepeatMonthlyDaySelection.ALL) (1..31).toSet() else days.filter { it > 0 }
                .toSet()

        return Triple(months, daySelection, selectedDays)
    }

    private fun AlarmMode.toUserSoundSelection(): UserSoundSelection = when (this) {
        AlarmMode.OFF -> UserSoundSelection.OFF
        AlarmMode.ONCE -> UserSoundSelection.ONCE
        AlarmMode.CONTINUOUS -> UserSoundSelection.CONTINUOUS
    }

    private fun AlarmMode.toUserVibrateSelection(): UserVibrateSelection = when (this) {
        AlarmMode.OFF -> UserVibrateSelection.OFF
        AlarmMode.ONCE -> UserVibrateSelection.ONCE
        AlarmMode.CONTINUOUS -> UserVibrateSelection.CONTINUOUS
    }

    private fun ReminderOffsetUnit.toTimeUnit(): TimeUnit = when (this) {
        ReminderOffsetUnit.MINUTES -> TimeUnit.MINUTES
        ReminderOffsetUnit.HOURS -> TimeUnit.HOURS
        ReminderOffsetUnit.DAYS -> TimeUnit.DAYS
        ReminderOffsetUnit.WEEKS -> TimeUnit.WEEKS
        ReminderOffsetUnit.MONTHS -> TimeUnit.MONTHS
    }
}

