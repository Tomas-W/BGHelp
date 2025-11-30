package com.example.bghelp.ui.screens.task.add

import android.content.Context
import com.example.bghelp.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object AddTaskValidator {
    fun validateTitle(title: String, context: Context): String? {
        if (title.isBlank()) {
            return context.getString(R.string.snackbar_title_invalid)
        }
        return null
    }

    fun validateDateTime(
        dateSelection: UserDateSelection,
        startDate: LocalDate,
        startTime: LocalTime,
        endDate: LocalDate?,
        isEndDateVisible: Boolean,
        endTime: LocalTime?,
        isEndTimeVisible: Boolean,
        context: Context
    ): String? {
        val allDay = dateSelection == UserDateSelection.ON
        val today = LocalDate.now()

        if (allDay) {
            if (startDate.isBefore(today)) {
                return context.getString(R.string.snackbar_start_time_invalid)
            }
        } else {
            val startDateTime = LocalDateTime.of(startDate, startTime)
            val now = LocalDateTime.now(ZoneId.systemDefault())
            if (startDateTime.isBefore(now)) {
                return context.getString(R.string.snackbar_start_time_invalid)
            }
        }

        if (isEndTimeVisible && endTime != null) {
            val effectiveEndDate = if (isEndDateVisible && endDate != null) endDate else startDate
            val startDateTime = if (allDay) {
                LocalDateTime.of(startDate, LocalTime.MIDNIGHT)
            } else {
                LocalDateTime.of(startDate, startTime)
            }
            val endDateTime = if (allDay) {
                LocalDateTime.of(effectiveEndDate, LocalTime.of(23, 59))
            } else {
                LocalDateTime.of(effectiveEndDate, endTime)
            }
            if (!endDateTime.isAfter(startDateTime)) {
                return context.getString(R.string.snackbar_end_time_invalid)
            }
        }

        return null
    }

    fun validateRepeat(
        repeatSelection: UserRepeatSelection,
        weeklyDays: Set<Int>,
        monthlyMonths: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        monthlyDays: Set<Int>,
        context: Context
    ): String? {
        if (repeatSelection == UserRepeatSelection.WEEKLY) {
            val normalizedDays = weeklyDays.mapNotNull { day ->
                runCatching { java.time.DayOfWeek.of(day) }.getOrNull()
            }
            if (normalizedDays.isEmpty()) {
                return context.getString(R.string.snackbar_repeat_days_invalid)
            }
        }

        if (repeatSelection == UserRepeatSelection.MONTHLY) {
            val validMonths = monthlyMonths.filter { it in 1..12 }
            if (validMonths.isEmpty()) {
                return context.getString(R.string.snackbar_repeat_months_invalid)
            }
            if (monthlyDaySelection == RepeatMonthlyDaySelection.SELECT) {
                val normalizedDays = monthlyDays.filter { it in 1..31 }
                if (normalizedDays.isEmpty()) {
                    return context.getString(R.string.snackbar_repeat_days_invalid)
                }
            }
        }

        return null
    }

    fun validateReminders(
        remindSelection: UserRemindSelection,
        dateSelection: UserDateSelection,
        startDate: LocalDate,
        startTime: LocalTime,
        startReminders: List<Reminder>,
        endReminders: List<Reminder>,
        endDate: LocalDate?,
        isEndDateVisible: Boolean,
        endTime: LocalTime?,
        isEndTimeVisible: Boolean,
        context: Context
    ): String? {
        if (remindSelection != UserRemindSelection.ON) return null

        if (startReminders.isEmpty() && endReminders.isEmpty()) {
            return context.getString(R.string.snackbar_no_reminders_selected)
        }

        val allDay = dateSelection == UserDateSelection.ON
        val startDateTime = if (allDay) {
            LocalDateTime.of(startDate, LocalTime.MIDNIGHT)
        } else {
            LocalDateTime.of(startDate, startTime)
        }
        val now = LocalDateTime.now(ZoneId.systemDefault())

        for (reminder in startReminders) {
            val reminderTime = calculateReminderTime(startDateTime, reminder)
            if (reminderTime.isBefore(now)) {
                return context.getString(R.string.snackbar_reminder_invalid)
            }
        }

        val endDateTime = if (isEndDateVisible || isEndTimeVisible) {
            val effectiveEndDate = endDate ?: startDate
            val effectiveEndTime = if (allDay) {
                LocalTime.of(23, 59)
            } else {
                endTime ?: startTime
            }
            LocalDateTime.of(effectiveEndDate, effectiveEndTime)
        } else {
            null
        }

        if (endDateTime != null) {
            for (reminder in endReminders) {
                val reminderTime = calculateReminderTime(endDateTime, reminder)
                if (reminderTime.isBefore(now)) {
                    return context.getString(R.string.snackbar_reminder_invalid)
                }
            }
        }

        return null
    }

    fun validateNote(
        noteSelection: UserNoteSelection,
        note: String,
        context: Context
    ): String? {
        if (noteSelection == UserNoteSelection.ON && note.isBlank()) {
            return context.getString(R.string.snackbar_no_note_selected)
        }
        return null
    }

    fun validateLocation(
        locationSelection: UserLocationSelection,
        selectedLocations: List<TaskLocation>,
        context: Context
    ): String? {
        if (locationSelection == UserLocationSelection.ON && selectedLocations.isEmpty()) {
            return context.getString(R.string.snackbar_no_location_selected)
        }
        return null
    }

    fun validateImage(
        imageSelection: UserImageSelection,
        selectedImage: TaskImageData?,
        context: Context
    ): String? {
        if (imageSelection == UserImageSelection.ON && selectedImage == null) {
            return context.getString(R.string.snackbar_no_image_selected)
        }
        return null
    }

    fun validateSound(
        soundSelection: UserSoundSelection,
        selectedAudioFile: String,
        context: Context
    ): String? {
        if ((soundSelection == UserSoundSelection.ONCE || soundSelection == UserSoundSelection.CONTINUOUS) && selectedAudioFile.isBlank()) {
            return context.getString(R.string.snackbar_no_alarm_selected)
        }
        return null
    }

    fun validateColor(
        colorSelection: UserColorSelection,
        selectedColorId: Int?,
        context: Context
    ): String? {
        if (colorSelection == UserColorSelection.ON && selectedColorId == null) {
            return context.getString(R.string.snackbar_no_color_selected)
        }
        return null
    }

    private fun calculateReminderTime(taskTime: LocalDateTime, reminder: Reminder): LocalDateTime {
        return when (reminder.timeUnit) {
            TimeUnit.MINUTES -> taskTime.minusMinutes(reminder.value.toLong())
            TimeUnit.HOURS -> taskTime.minusHours(reminder.value.toLong())
            TimeUnit.DAYS -> taskTime.minusDays(reminder.value.toLong())
            TimeUnit.WEEKS -> taskTime.minusWeeks(reminder.value.toLong())
            TimeUnit.MONTHS -> taskTime.minusMonths(reminder.value.toLong())
        }
    }
}


