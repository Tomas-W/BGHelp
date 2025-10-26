package com.example.bghelp.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun getEpochRange(date: LocalDateTime = LocalDateTime.now()): Pair<Long, Long> {
    val zone = ZoneId.systemDefault()
    val today = date.toLocalDate()
    val startOfDay = today.atStartOfDay(zone).toInstant().toEpochMilli()
    val endOfDay = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    return Pair(startOfDay, endOfDay)
}

fun LocalDateTime.toDayHeader(): String {
    val date = this.toLocalDate()
    val today = LocalDate.now()
    val monthDayFormatter = DateTimeFormatter.ofPattern("MMM dd") // "Jan 01"
    val weekdayFormatter = DateTimeFormatter.ofPattern("EEE") // "Mon"

    val prefix = when (date) {
        today.minusDays(1) -> "Yesterday"
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        else -> date.format(weekdayFormatter)
    }

    return "$prefix, ${date.format(monthDayFormatter)}"
}


fun LocalDateTime.isInFuture(): Boolean {
    return this.isAfter(LocalDateTime.now())
}

fun LocalDateTime.toTaskTime(): String {
    return this.toLocalTime()
        .format(DateTimeFormatter.ofPattern("HH:mm")) // 15:44
}