package com.example.bghelp.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Long.toDayHeader(): String {
    val date = Instant.ofEpochMilli(this)  // Use milliseconds
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val today = LocalDate.now()
    val monthDayFormatter = DateTimeFormatter.ofPattern("MMM dd")
    val weekdayFormatter = DateTimeFormatter.ofPattern("EEE") // "Mon", "Tue", etc.

    val prefix = when (date) {
        today.minusDays(1) -> "Yesterday"
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        else -> date.format(weekdayFormatter)
    }

    return "$prefix, ${date.format(monthDayFormatter)}"
}

fun Long.isInFuture(): Boolean {
    val providedTime = this
    val now: Long = Instant.now().toEpochMilli()  // Use milliseconds
    return providedTime > now
}

fun Long.toTaskTime(): String {
    return Instant.ofEpochMilli(this)  // Use milliseconds
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(DateTimeFormatter.ofPattern("HH:mm"))
}

fun getEpochRange(date: Instant = Instant.now()): Pair<Long, Long> {
    val zone = ZoneId.systemDefault()
    val today = date.atZone(zone).toLocalDate()
    val startOfDay = today.atStartOfDay(zone).toInstant().toEpochMilli()  // Use milliseconds
    val endOfDay = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1  // Use milliseconds
    return Pair(startOfDay, endOfDay)
}
