package com.example.bghelp.utils

import com.example.bghelp.ui.screens.task.add.RepeatMonthlyDaySelection
import com.example.bghelp.ui.screens.task.add.UserRepeatSelection
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

object RepeatRuleBuilder {
    fun buildRRule(
        selection: UserRepeatSelection,
        weeklyDays: Set<Int>,
        weeklyInterval: Int,
        monthlyMonths: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        monthlyDays: Set<Int>,
        endDate: LocalDate? = null,
        isEndDateVisible: Boolean = false
    ): String? {
        return when (selection) {
            UserRepeatSelection.OFF -> {
                if (isEndDateVisible && endDate != null) {
                    buildDailyRRule(endDate = endDate)
                } else {
                    null
                }
            }
            UserRepeatSelection.WEEKLY -> buildWeeklyRRule(
                weeklyDays = weeklyDays,
                weeklyInterval = weeklyInterval,
                endDate = endDate
            )

            UserRepeatSelection.MONTHLY -> buildMonthlyRRule(
                selectedMonths = monthlyMonths,
                monthlyDaySelection = monthlyDaySelection,
                selectedDays = monthlyDays,
                endDate = endDate
            )
        }
    }

    private fun buildDailyRRule(
        endDate: LocalDate
    ): String {
        return buildString {
            append("FREQ=DAILY")
            append(";UNTIL=${formatUntilDate(endDate)}")
        }
    }

    private fun buildWeeklyRRule(
        weeklyDays: Set<Int>,
        weeklyInterval: Int,
        endDate: LocalDate? = null
    ): String? {
        val normalizedDays = weeklyDays.mapNotNull { it.toDayOfWeekOrNull() }
        if (normalizedDays.isEmpty()) {
            return null
        }
        
        val dayTokens = normalizedDays
            .distinct()
            .sortedBy { it.value }
            .joinToString(",") { it.toRRuleToken() }

        return buildString {
            append("FREQ=WEEKLY")
            if (weeklyInterval > 1) {
                append(";INTERVAL=$weeklyInterval")
            }
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
        endDate: LocalDate? = null
    ): String? {
        val months = selectedMonths
            .filter { it in 1..12 }
            .distinct()
            .sorted()
            .let {
                when {
                    it.isEmpty() -> return null
                    it.size == 12 -> emptyList()
                    else -> it
                }
            }

        val days = when (monthlyDaySelection) {
            RepeatMonthlyDaySelection.ALL -> (1..31).toList()
            RepeatMonthlyDaySelection.SELECT -> {
                selectedDays.filter { it in 1..31 }.distinct().sorted()
            }

            RepeatMonthlyDaySelection.LAST -> listOf(-1)
        }

        if (days.isEmpty()) {
            return null
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
        return String.format(Locale.ROOT, "%04d%02d%02d", date.year, date.monthValue, date.dayOfMonth)
    }
}
