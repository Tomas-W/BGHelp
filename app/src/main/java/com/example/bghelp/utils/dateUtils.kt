package com.example.bghelp.utils

import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.example.bghelp.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

fun getEpochRange(date: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())): Pair<Long, Long> {
    val zone = ZoneId.systemDefault()
    val today = date.toLocalDate()
    val startOfDay = today.atStartOfDay(zone).toInstant().toEpochMilli()
    val endOfDay = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    return Pair(startOfDay, endOfDay)
}

@Composable
fun LocalDateTime.toDayHeader(): String {
    val date = this.toLocalDate()
    val today = getToday()  // Use cached today instead of LocalDate.now()
    val locale = Locale.getDefault()
    val monthDayFormatter = remember(locale) { DateTimeFormatter.ofPattern("MMM dd", locale) }
    val weekdayFormatter = remember(locale) { DateTimeFormatter.ofPattern("EEEE", locale) }

    val prefix = when (date) {
        today.minusDays(1) -> stringResource(R.string.yesterday)
        today -> stringResource(R.string.today)
        today.plusDays(1) -> stringResource(R.string.tomorrow)
        else -> date.format(weekdayFormatter)
    }

    return "$prefix ${date.format(monthDayFormatter)}"
}

fun LocalDateTime.isInFuture(): Boolean {
    return this.isAfter(LocalDateTime.now(ZoneId.systemDefault()))
}

fun LocalDateTime.toTaskTime(): String {
    return formatTime(this.toLocalTime())
}

// Centralized date/time formatting helpers

fun formatTime(time: LocalTime, locale: Locale = Locale.getDefault()): String {
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
    return time.format(formatter)
}

fun formatDuration(hours: Int, minutes: Int, locale: Locale = Locale.getDefault()): String {
    val measures = buildList {
        if (hours > 0) add(Measure(hours, MeasureUnit.HOUR))
        if (minutes > 0) add(Measure(minutes, MeasureUnit.MINUTE))
    }.takeIf { it.isNotEmpty() } ?: listOf(Measure(0, MeasureUnit.MINUTE))
    val formatter = MeasureFormat.getInstance(locale, MeasureFormat.FormatWidth.SHORT)
    return formatter.formatMeasures(*measures.toTypedArray())
}

fun formatDuration(totalSeconds: Int, locale: Locale = Locale.getDefault()): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    val (value, unit) = when {
        safeSeconds < 60 -> safeSeconds to MeasureUnit.SECOND
        safeSeconds < 3600 -> safeSeconds / 60 to MeasureUnit.MINUTE
        safeSeconds < 86400 -> safeSeconds / 3600 to MeasureUnit.HOUR
        safeSeconds < 604800 -> safeSeconds / 86400 to MeasureUnit.DAY
        safeSeconds < 2592000 -> safeSeconds / 604800 to MeasureUnit.WEEK
        else -> safeSeconds / 2592000 to MeasureUnit.MONTH
    }
    val formatter = MeasureFormat.getInstance(locale, MeasureFormat.FormatWidth.SHORT)
    return formatter.format(Measure(value, unit))
}

fun Int.formatSnoozeDuration(locale: Locale = Locale.getDefault()): String {
    return formatDuration(this, locale)
}

