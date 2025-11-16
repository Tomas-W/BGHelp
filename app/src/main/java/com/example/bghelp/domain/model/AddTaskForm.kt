package com.example.bghelp.domain.model

import com.example.bghelp.ui.screens.task.add.RepeatMonthlyDaySelection
import com.example.bghelp.ui.screens.task.add.UserDateSelection
import com.example.bghelp.ui.screens.task.add.UserRemindSelection
import com.example.bghelp.ui.screens.task.add.UserRepeatSelection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

data class AddTaskForm(
    val title: String = "",
    val info: String = "",
    val note: String = "",
    val dateSelection: UserDateSelection = UserDateSelection.OFF,
    val startDate: LocalDate = LocalDate.now().plusDays(1),
    val endDate: LocalDate? = null,
    val isEndDateVisible: Boolean = false,
    val startTime: LocalTime = LocalTime.of(12, 0),
    val endTime: LocalTime? = null,
    val isEndTimeVisible: Boolean = false,
    val currentMonth: YearMonth = YearMonth.now(),
    val repeatSelection: UserRepeatSelection = UserRepeatSelection.OFF,
    val weeklySelectedDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val weeklyIntervalWeeks: Int = 1,
    val monthlySelectedMonths: Set<Int> = (1..12).toSet(),
    val monthlyDaySelection: RepeatMonthlyDaySelection = RepeatMonthlyDaySelection.ALL,
    val monthlySelectedDays: Set<Int> = (1..31).toSet(),
    val repeatRRule: String? = null,
    val remindSelection: UserRemindSelection = UserRemindSelection.OFF
)


