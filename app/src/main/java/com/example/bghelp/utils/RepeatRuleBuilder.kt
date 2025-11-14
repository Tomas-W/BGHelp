package com.example.bghelp.utils

import com.example.bghelp.ui.screens.task.add.RepeatMonthlyDaySelection
import com.example.bghelp.ui.screens.task.add.UserRepeatSelection
import java.time.DayOfWeek
import java.time.LocalDate

object RepeatRuleBuilder {
    fun buildRRule(
        selection: UserRepeatSelection,
        weeklyDays: Set<Int>,
        weeklyInterval: Int,
        monthlyMonths: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        monthlyDays: Set<Int>,
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): String? {
        return when (selection) {
            UserRepeatSelection.OFF -> null
            UserRepeatSelection.WEEKLY -> buildWeeklyRRule(
                weeklyDays = weeklyDays,
                weeklyInterval = weeklyInterval,
                startDate = startDate,
                endDate = endDate
            )
            UserRepeatSelection.MONTHLY -> buildMonthlyRRule(
                selectedMonths = monthlyMonths,
                monthlyDaySelection = monthlyDaySelection,
                selectedDays = monthlyDays,
                startDate = startDate,
                endDate = endDate
            )
        }
    }

    private fun buildWeeklyRRule(
        weeklyDays: Set<Int>,
        weeklyInterval: Int,
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): String {
        val normalizedDays = weeklyDays.mapNotNull { it.toDayOfWeekOrNull() }
        // Don't add fallback - if user deselected all days, create empty BYDAY to filter out task
        val dayTokens = normalizedDays
            .distinct()
            .sortedBy { it.value }
            .joinToString(",") { it.toRRuleToken() }

        return buildString {
            append("FREQ=WEEKLY")
            if (weeklyInterval > 1) {
                append(";INTERVAL=$weeklyInterval")
            }
            // BYDAY may be empty if user deselected all days - this will be filtered out in TaskRepository
            append(";BYDAY=$dayTokens")
            if (endDate != null) {
                append(";UNTIL=${formatUntilDate(endDate)}")
            }
        }
    }

    private fun buildMonthlyRRule(
        selectedMonths: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        selectedDays: Set<Int>,
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): String {
        val months = selectedMonths
            .filter { it in 1..12 }
            .distinct()
            .sorted()
            .let {
                when {
                    it.isEmpty() -> listOf(startDate.monthValue)
                    it.size == 12 -> emptyList()
                    else -> it
                }
            }

        val days = when (monthlyDaySelection) {
            RepeatMonthlyDaySelection.ALL -> (1..31).toList()
            RepeatMonthlyDaySelection.SELECT -> {
                // Don't add fallback - if user deselected all days, create empty BYMONTHDAY to filter out task
                selectedDays.filter { it in 1..31 }.distinct().sorted()
            }
            RepeatMonthlyDaySelection.LAST -> listOf(-1)
        }

        return buildString {
            append("FREQ=MONTHLY")
            if (months.isNotEmpty()) {
                append(";BYMONTH=${months.joinToString(",")}")
            }
            if (days.isNotEmpty()) {
                append(";BYMONTHDAY=${days.joinToString(",")}")
            }
            if (endDate != null) {
                append(";UNTIL=${formatUntilDate(endDate)}")
            }
        }
    }

    private fun Int.toDayOfWeekOrNull(): DayOfWeek? = runCatching {
        DayOfWeek.of(this)
    }.getOrNull()

    private fun DayOfWeek.toRRuleToken(): String = when (this) {
        DayOfWeek.MONDAY -> "MO"
        DayOfWeek.TUESDAY -> "TU"
        DayOfWeek.WEDNESDAY -> "WE"
        DayOfWeek.THURSDAY -> "TH"
        DayOfWeek.FRIDAY -> "FR"
        DayOfWeek.SATURDAY -> "SA"
        DayOfWeek.SUNDAY -> "SU"
    }

    private fun formatUntilDate(date: LocalDate): String {
        // Format as YYYYMMDD for iCal UNTIL parameter
        return String.format("%04d%02d%02d", date.year, date.monthValue, date.dayOfMonth)
    }
}

