package com.example.bghelp.domain.service

import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.ReminderOffsetUnit
import com.example.bghelp.domain.model.Task
import com.example.bghelp.domain.model.TaskReminderEntry
import com.example.bghelp.domain.model.UpcomingAlert
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object AlertCalculator {
    fun calculateReminderTriggerTime(task: Task, reminder: TaskReminderEntry): LocalDateTime? {
        val targetTime = when (reminder.type) {
            ReminderKind.START -> task.date
            ReminderKind.END -> task.endDate ?: return null
        }

        return when (reminder.offsetUnit) {
            ReminderOffsetUnit.MINUTES -> targetTime.minus(Duration.ofMinutes(reminder.offsetValue.toLong()))
            ReminderOffsetUnit.HOURS -> targetTime.minus(Duration.ofHours(reminder.offsetValue.toLong()))
            ReminderOffsetUnit.DAYS -> targetTime.minus(Duration.ofDays(reminder.offsetValue.toLong()))
            ReminderOffsetUnit.WEEKS -> targetTime.minus(Duration.ofDays(reminder.offsetValue.toLong() * 7))
            ReminderOffsetUnit.MONTHS -> targetTime.minus(reminder.offsetValue.toLong(), ChronoUnit.MONTHS)
        }
    }

    fun getUpcomingAlertsForTask(task: Task): List<UpcomingAlert> {
        val alerts = mutableListOf<UpcomingAlert>()
        val now = LocalDateTime.now(ZoneId.systemDefault())

        // Add reminders from task.reminders
        for (reminder in task.reminders) {
            val triggerTime = calculateReminderTriggerTime(task, reminder) ?: continue
            if (triggerTime.isAfter(now)) {
                alerts.add(UpcomingAlert(task.id, triggerTime, reminder))
            }
        }

        // Add task start time as alert if task is not all-day
        if (!task.allDay && task.date.isAfter(now)) {
            alerts.add(UpcomingAlert(task.id, task.date, null))
        }

        return alerts
    }

    fun getUpcomingAlertsForRecurringTask(baseTask: Task): List<UpcomingAlert> {
        if (baseTask.rrule == null) return emptyList()

        val now = LocalDateTime.now(ZoneId.systemDefault())
        val futureWindow = now.plusYears(1).plusDays(1)

        // Generate first occurrence
        val occurrences = RecurrenceCalculator.generateOccurrences(
            task = baseTask,
            ruleString = baseTask.rrule,
            windowStart = now,
            windowEndExclusive = futureWindow
        )

        val firstOccurrence = occurrences.firstOrNull() ?: return emptyList()

        // Get alerts for first occurrence
        return getUpcomingAlertsForTask(firstOccurrence)
    }
}

