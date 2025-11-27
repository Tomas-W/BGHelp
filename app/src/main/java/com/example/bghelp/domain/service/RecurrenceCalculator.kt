package com.example.bghelp.domain.service

import com.example.bghelp.domain.model.Task
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Locale

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
            "DAILY" -> Frequency.DAILY
            "WEEKLY" -> Frequency.WEEKLY
            "MONTHLY" -> Frequency.MONTHLY
            else -> return null
        }

        val interval = values["INTERVAL"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1

        val until = values["UNTIL"]?.let { untilStr ->
            parseUntilDate(untilStr)
        }

        val exDates = values["EXDATE"]?.split(",")
            ?.mapNotNull { parseUntilDate(it) }
            ?.toSet()
            ?: emptySet()

        return when (frequency) {
            Frequency.DAILY -> {
                RecurrenceRule(
                    frequency = frequency,
                    interval = interval,
                    until = until,
                    exDates = exDates
                )
            }

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
                    until = until,
                    exDates = exDates
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
                    until = until,
                    exDates = exDates
                )
            }
        }
    }

    fun occursOnDate(task: Task, rule: RecurrenceRule, date: LocalDate): Boolean {
        if (rule.until != null && date.isAfter(rule.until)) {
            return false
        }
        if (date in rule.exDates) {
            return false
        }

        return when (rule.frequency) {
            Frequency.DAILY -> occursDaily(task, rule, date)
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

    private fun occursDaily(task: Task, rule: RecurrenceRule, date: LocalDate): Boolean {
        val baseDate = task.date.toLocalDate()
        if (date.isBefore(baseDate)) return false
        if (rule.until != null && date.isAfter(rule.until)) return false

        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(baseDate, date)
        if (daysBetween < 0) return false

        return daysBetween % rule.interval == 0L
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
        val now = LocalDateTime.now(ZoneId.systemDefault())
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

    fun addExDateToRRule(rrule: String, exDate: LocalDate): String {
        val rule = parseRRule(rrule) ?: return rrule
        val updatedExDates = rule.exDates + exDate
        return buildRRuleString(rule.copy(exDates = updatedExDates))
    }

    private fun buildRRuleString(rule: RecurrenceRule): String {
        val parts = mutableListOf<String>()
        parts.add("FREQ=${rule.frequency.name}")
        if (rule.interval > 1) {
            parts.add("INTERVAL=${rule.interval}")
        }
        if (rule.until != null) {
            val untilStr = String.format(Locale.getDefault(), "%04d%02d%02d", rule.until.year, rule.until.monthValue, rule.until.dayOfMonth)
            parts.add("UNTIL=$untilStr")
        }
        if (rule.byDay.isNotEmpty()) {
            val byDayStr = rule.byDay.joinToString(",") { it.toRRuleToken() }
            parts.add("BYDAY=$byDayStr")
        }
        if (rule.byMonth.isNotEmpty()) {
            val byMonthStr = rule.byMonth.joinToString(",") { it.toString() }
            parts.add("BYMONTH=$byMonthStr")
        }
        if (rule.byMonthDay.isNotEmpty()) {
            val byMonthDayStr = rule.byMonthDay.joinToString(",") { it.toString() }
            parts.add("BYMONTHDAY=$byMonthDayStr")
        }
        if (rule.exDates.isNotEmpty()) {
            val exDateStr = rule.exDates.sorted().joinToString(",") { date ->
                String.format(Locale.getDefault(), "%04d%02d%02d", date.year, date.monthValue, date.dayOfMonth)
            }
            parts.add("EXDATE=$exDateStr")
        }
        return parts.joinToString(";")
    }

    private fun DayOfWeek.toRRuleToken(): String = when (this) {
        DayOfWeek.MONDAY -> "MO"
        DayOfWeek.TUESDAY -> "TU"
        DayOfWeek.WEDNESDAY -> "WE"
        DayOfWeek.THURSDAY -> "TH"
        DayOfWeek.FRIDAY -> "FR"
        DayOfWeek.SATURDAY -> "SA"
        DayOfWeek.SUNDAY -> "SU"
    }

    data class RecurrenceRule(
        val frequency: Frequency,
        val interval: Int,
        val byDay: Set<DayOfWeek> = emptySet(),
        val byMonth: Set<Int> = emptySet(),
        val byMonthDay: List<Int> = emptyList(),
        val until: LocalDate? = null,
        val exDates: Set<LocalDate> = emptySet()
    )

    enum class Frequency {
        DAILY,
        WEEKLY,
        MONTHLY
    }
}


