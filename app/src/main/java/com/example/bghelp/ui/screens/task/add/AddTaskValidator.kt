package com.example.bghelp.ui.screens.task.add

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object AddTaskValidator {
    fun validateTitle(title: String): String? {
        if (title.isBlank()) {
            return AddTaskStrings.VALIDATION_TITLE_EMPTY
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
        isEndTimeVisible: Boolean
    ): String? {
        val allDay = dateSelection == UserDateSelection.ON
        val today = LocalDate.now()

        if (allDay) {
            if (startDate.isBefore(today)) {
                return AddTaskStrings.VALIDATION_START_TIME_PAST
            }
        } else {
            val startDateTime = LocalDateTime.of(startDate, startTime)
            val now = LocalDateTime.now(ZoneId.systemDefault())
            if (startDateTime.isBefore(now)) {
                return AddTaskStrings.VALIDATION_START_TIME_PAST
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
                return AddTaskStrings.VALIDATION_END_TIME_BEFORE_START
            }
        }

        return null
    }

    fun validateRepeat(
        repeatSelection: UserRepeatSelection,
        weeklyDays: Set<Int>,
        monthlyMonths: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        monthlyDays: Set<Int>
    ): String? {
        if (repeatSelection == UserRepeatSelection.WEEKLY) {
            val normalizedDays = weeklyDays.mapNotNull { day ->
                runCatching { java.time.DayOfWeek.of(day) }.getOrNull()
            }
            if (normalizedDays.isEmpty()) {
                return AddTaskStrings.VALIDATION_REPEAT_MIN_DAYS
            }
        }

        if (repeatSelection == UserRepeatSelection.MONTHLY) {
            val validMonths = monthlyMonths.filter { it in 1..12 }
            if (validMonths.isEmpty()) {
                return AddTaskStrings.VALIDATION_REPEAT_MIN_MONTHS
            }
            if (monthlyDaySelection == RepeatMonthlyDaySelection.SELECT) {
                val normalizedDays = monthlyDays.filter { it in 1..31 }
                if (normalizedDays.isEmpty()) {
                    return AddTaskStrings.VALIDATION_REPEAT_MIN_DAYS
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
        isEndTimeVisible: Boolean
    ): String? {
        if (remindSelection != UserRemindSelection.ON) return null

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
                return AddTaskStrings.VALIDATION_REMINDERS_PAST
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
                    return AddTaskStrings.VALIDATION_REMINDERS_PAST
                }
            }
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


