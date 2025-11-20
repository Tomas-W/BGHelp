package com.example.bghelp.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// Cache today's date to avoid repeated calls
private var cachedToday: LocalDate? = null
private var lastCacheTime: Long = 0L

private fun getToday(): LocalDate {
    val currentTime = System.currentTimeMillis()
    // Cache for 60 seconds to avoid excessive date changes
    if (cachedToday == null || currentTime - lastCacheTime > 60_000) {
        cachedToday = LocalDate.now()
        lastCacheTime = currentTime
    }
    return cachedToday!!
}

fun getEpochRange(date: LocalDateTime = LocalDateTime.now()): Pair<Long, Long> {
    val zone = ZoneId.systemDefault()
    val today = date.toLocalDate()
    val startOfDay = today.atStartOfDay(zone).toInstant().toEpochMilli()
    val endOfDay = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    return Pair(startOfDay, endOfDay)
}

fun LocalDateTime.toDayHeader(): String {
    val date = this.toLocalDate()
    val today = getToday()  // Use cached today instead of LocalDate.now()
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
    return formatTime(this.toLocalTime())
}

// Centralized date/time formatting helpers

fun formatTime(time: LocalTime): String {
    return time.format(DateTimeFormatter.ofPattern("HH:mm"))
}

fun Int.formatSnoozeDuration(): String {
    return when {
        this < 60 -> "${this}s"
        this < 3600 -> "${this / 60}m"
        this < 86400 -> "${this / 3600}h"
        this < 604800 -> "${this / 86400}d"
        this < 2592000 -> "${this / 604800}w"
        else -> "${this / 2592000}M"
    }
}

