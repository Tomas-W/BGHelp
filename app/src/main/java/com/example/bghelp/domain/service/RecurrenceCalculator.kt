package com.example.bghelp.domain.service

import com.example.bghelp.domain.model.Task
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

object RecurrenceCalculator {
    fun parseRRule(rrule: String): RecurrenceRule? {
        if (rrule.isBlank()) return null

        val components = rrule.split(";")
        val values = buildMap {
            components.forEach { component ->
                val separatorIndex = component.indexOf("=")
                if (separatorIndex > 0) {
                    val key = component.substring(0, separatorIndex).uppercase()
                    val value = component.substring(separatorIndex + 1)
                    put(key, value)
                }
            }
        }

        val frequency = when (values["FREQ"]?.uppercase()) {
            "WEEKLY" -> Frequency.WEEKLY
            "MONTHLY" -> Frequency.MONTHLY
            else -> return null
        }

        val interval = values["INTERVAL"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1

        val until = values["UNTIL"]?.let { untilStr ->
            parseUntilDate(untilStr)
        }

        return when (frequency) {
            Frequency.WEEKLY -> {
                val byDay = values["BYDAY"]
                    ?.split(",")
                    ?.mapNotNull { parseDayOfWeek(it) }
                    ?.toSet()
                    ?: emptySet()
                RecurrenceRule(
                    frequency = frequency,
                    interval = interval,
                    byDay = byDay,
                    until = until
                )
            }

            Frequency.MONTHLY -> {
                val byMonth = values["BYMONTH"]
                    ?.takeIf { it.isNotBlank() }
                    ?.split(",")
                    ?.mapNotNull { it.toIntOrNull() }
                    ?.toSet()
                    ?: emptySet()

                val byMonthDay = values["BYMONTHDAY"]
                    ?.takeIf { it.isNotBlank() }
                    ?.split(",")
                    ?.mapNotNull { it.toIntOrNull() }
                    ?: emptyList()

                RecurrenceRule(
                    frequency = frequency,
                    interval = interval,
                    byMonth = byMonth,
                    byMonthDay = byMonthDay,
                    until = until
                )
            }
        }
    }

    fun occursOnDate(task: Task, rule: RecurrenceRule, date: LocalDate): Boolean {
        if (rule.until != null && date.isAfter(rule.until)) {
            return false
        }

        return when (rule.frequency) {
            Frequency.WEEKLY -> occursWeekly(task, rule, date)
            Frequency.MONTHLY -> occursMonthly(task, rule, date)
        }
    }

    fun generateOccurrences(
        task: Task,
        ruleString: String,
        windowStart: LocalDateTime,
        windowEndExclusive: LocalDateTime
    ): List<Task> {
        val rule = parseRRule(ruleString) ?: return emptyList()
        val occurrences = mutableListOf<Task>()
        val lastDate = windowEndExclusive.minusNanos(1).toLocalDate()
        var currentDate = windowStart.toLocalDate()

        while (!currentDate.isAfter(lastDate)) {
            if (occursOnDate(task, rule, currentDate)) {
                val occurrenceStart = LocalDateTime.of(currentDate, task.date.toLocalTime())
                if (!occurrenceStart.isBefore(windowStart) && occurrenceStart.isBefore(windowEndExclusive)) {
                    occurrences.add(task.createOccurrence(occurrenceStart))
                }
            }
            currentDate = currentDate.plusDays(1)
        }

        return occurrences
    }

    private fun occursWeekly(task: Task, rule: RecurrenceRule, date: LocalDate): Boolean {
        val baseDate = task.date.toLocalDate()
        if (date.isBefore(baseDate)) return false
        if (rule.byDay.isEmpty()) return false
        if (date.dayOfWeek !in rule.byDay) return false

        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(baseDate, date)
        if (daysBetween < 0) return false

        val weeksBetween = daysBetween / 7
        return weeksBetween % rule.interval == 0L
    }

    private fun occursMonthly(task: Task, rule: RecurrenceRule, date: LocalDate): Boolean {
        val baseDate = task.date.toLocalDate()
        if (date.isBefore(baseDate)) return false

        val monthsBetween = java.time.temporal.ChronoUnit.MONTHS.between(
            YearMonth.from(baseDate),
            YearMonth.from(date)
        )
        if (monthsBetween < 0) return false
        if (monthsBetween % rule.interval != 0L) return false

        if (rule.byMonth.isNotEmpty() && date.monthValue !in rule.byMonth) return false
        if (rule.byMonthDay.isEmpty()) return false

        val matchesDay = rule.byMonthDay.any { day ->
            when {
                day == -1 -> date == date.with(TemporalAdjusters.lastDayOfMonth())
                day > 0 -> date.dayOfMonth == day
                else -> false
            }
        }

        return matchesDay
    }

    private fun parseDayOfWeek(token: String): DayOfWeek? =
        when (token.uppercase()) {
            "MO" -> DayOfWeek.MONDAY
            "TU" -> DayOfWeek.TUESDAY
            "WE" -> DayOfWeek.WEDNESDAY
            "TH" -> DayOfWeek.THURSDAY
            "FR" -> DayOfWeek.FRIDAY
            "SA" -> DayOfWeek.SATURDAY
            "SU" -> DayOfWeek.SUNDAY
            else -> null
        }

    private fun parseUntilDate(untilStr: String): LocalDate? {
        if (untilStr.length == 8) {
            val year = untilStr.substring(0, 4).toIntOrNull()
            val month = untilStr.substring(4, 6).toIntOrNull()
            val day = untilStr.substring(6, 8).toIntOrNull()
            if (year != null && month != null && day != null) {
                return try {
                    LocalDate.of(year, month, day)
                } catch (_: Exception) {
                    null
                }
            }
        }
        return null
    }

    private fun Task.createOccurrence(start: LocalDateTime): Task {
        val duration = endDate?.let { Duration.between(date, it) }
        val newEnd = duration?.let { start.plus(it) }
        val now = LocalDateTime.now()
        val isExpired = if (allDay && newEnd != null) {
            newEnd.isBefore(now)
        } else {
            start.isBefore(now)
        }
        return copy(
            date = start,
            endDate = newEnd,
            expired = isExpired
        )
    }

    data class RecurrenceRule(
        val frequency: Frequency,
        val interval: Int,
        val byDay: Set<DayOfWeek> = emptySet(),
        val byMonth: Set<Int> = emptySet(),
        val byMonthDay: List<Int> = emptyList(),
        val until: LocalDate? = null
    )

    enum class Frequency {
        WEEKLY,
        MONTHLY
    }
}


