package com.example.bghelp.domain.model

import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.SchedulableItem
import java.time.LocalDateTime
import java.time.ZoneId

data class CreateTask(
    val date: LocalDateTime,
    val title: String,
    val description: String? = null,
    val expired: Boolean = false,
    val alarmName: String? = null,
    val sound: AlarmMode = AlarmMode.OFF,
    val vibrate: AlarmMode = AlarmMode.OFF,
    val snoozeTime: Int = 0
)

data class Task(
    override val id: Int,
    override val date: LocalDateTime,
    override val title: String,
    override val description: String?,
    val expired: Boolean,
    val alarmName: String?,
    override val sound: AlarmMode,
    override val vibrate: AlarmMode,
    override val snoozeTime: Int
) : SchedulableItem {
    // Removed isExpired - it called LocalDateTime.now() on every access
    // Use date.isBefore(LocalDateTime.now()) directly in UI when needed
    
    val hasAlarm: Boolean
        get() = sound != AlarmMode.OFF || vibrate != AlarmMode.OFF
}
