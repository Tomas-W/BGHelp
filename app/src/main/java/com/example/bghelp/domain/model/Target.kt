package com.example.bghelp.domain.model

import java.time.LocalDateTime
import java.time.ZoneId

data class CreateTarget(
    val date: LocalDateTime,
    val title: String,
    val description: String? = null,
    val expired: Boolean = false,
    val coordinates: List<Coordinate> = emptyList(),
    val alertDistance: Int = 0,
    val alarmName: String? = null,
    val sound: AlarmMode = AlarmMode.OFF,
    val vibrate: AlarmMode = AlarmMode.OFF,
    val snoozeTime: Int = 0
)

data class Target(
    val id: Int,
    val date: LocalDateTime,
    val title: String,
    val description: String?,
    val expired: Boolean,
    val coordinates: List<Coordinate>,
    val alertDistance: Int,
    val alarmName: String?,
    val sound: AlarmMode,
    val vibrate: AlarmMode,
    val snoozeTime: Int
) {
    val hasAlarm: Boolean
        get() = sound != AlarmMode.OFF || vibrate != AlarmMode.OFF
}
