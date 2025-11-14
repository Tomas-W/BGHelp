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
        startDate: LocalDate
    ): String? {
        return when (selection) {
            UserRepeatSelection.OFF -> null
            UserRepeatSelection.WEEKLY -> buildWeeklyRRule(
                weeklyDays = weeklyDays,
                weeklyInterval = weeklyInterval,
                startDate = startDate
            )
            UserRepeatSelection.MONTHLY -> buildMonthlyRRule(
                selectedMonths = monthlyMonths,
                monthlyDaySelection = monthlyDaySelection,
                selectedDays = monthlyDays,
                startDate = startDate
            )
        }
    }

    private fun buildWeeklyRRule(
        weeklyDays: Set<Int>,
        weeklyInterval: Int,
        startDate: LocalDate
    ): String {
        val normalizedDays = weeklyDays.mapNotNull { it.toDayOfWeekOrNull() }
        val effectiveDays = if (normalizedDays.isEmpty()) {
            listOf(startDate.dayOfWeek)
        } else {
            normalizedDays
        }
        val dayTokens = effectiveDays
            .distinct()
            .sortedBy { it.value }
            .joinToString(",") { it.toRRuleToken() }

        return buildString {
            append("FREQ=WEEKLY")
            if (weeklyInterval > 1) {
                append(";INTERVAL=$weeklyInterval")
            }
            append(";BYDAY=$dayTokens")
        }
    }

    private fun buildMonthlyRRule(
        selectedMonths: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        selectedDays: Set<Int>,
        startDate: LocalDate
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
                val normalized = selectedDays.filter { it in 1..31 }.distinct().sorted()
                if (normalized.isEmpty()) listOf(startDate.dayOfMonth) else normalized
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
}

