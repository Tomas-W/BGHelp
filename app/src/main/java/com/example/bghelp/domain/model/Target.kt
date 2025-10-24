package com.example.bghelp.domain.model

import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.utils.Coordinate
import com.example.bghelp.utils.SchedulableItem
import java.time.LocalDateTime
import java.time.ZoneId

data class CreateTarget(
    val date: LocalDateTime,
    val message: String,
    val expired: Boolean = false,
    val coordinates: List<Coordinate> = emptyList(),
    val alertDistance: Int = 0,
    val alarmName: String? = null,
    val sound: AlarmMode = AlarmMode.OFF,
    val vibrate: AlarmMode = AlarmMode.OFF,
    val snoozeTime: Int = 0
)

data class Target(
    override val id: Int,
    val dateTime: LocalDateTime,
    override val message: String,
    val expired: Boolean,
    val coordinates: List<Coordinate>,
    val alertDistance: Int,
    val alarmName: String?,
    override val sound: AlarmMode,
    override val vibrate: AlarmMode,
    override val snoozeTime: Int
) : SchedulableItem {
    // SchedulableItem properties
    override val date: Long
        get() = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val isExpired: Boolean
        get() = dateTime.isBefore(LocalDateTime.now())

    val hasAlarm: Boolean
        get() = sound != AlarmMode.OFF || vibrate != AlarmMode.OFF
}