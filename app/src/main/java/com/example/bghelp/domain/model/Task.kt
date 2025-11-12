package com.example.bghelp.domain.model

import java.time.LocalDateTime
import java.time.ZoneId

enum class TaskColorOption {
    DEFAULT,
    RED,
    GREEN,
    YELLOW,
    CYAN,
    MAGENTA
}

enum class TaskImageSourceOption {
    GALLERY,
    CAMERA
}

data class TaskImageAttachment(
    val uri: String?,
    val displayName: String?,
    val source: TaskImageSourceOption
)

data class TaskLocationEntry(
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val name: String,
    val order: Int = 0
)

enum class ReminderKind {
    START,
    END
}

enum class ReminderOffsetUnit {
    MINUTES,
    HOURS,
    DAYS,
    WEEKS,
    MONTHS
}

data class TaskReminderEntry(
    val id: Int = 0,
    val type: ReminderKind,
    val offsetValue: Int,
    val offsetUnit: ReminderOffsetUnit
)

data class CreateTask(
    val date: LocalDateTime,
    val endDate: LocalDateTime? = null,
    val allDay: Boolean = false,
    val title: String,
    val description: String? = null,
    val note: String? = null,
    val rrule: String? = null,
    val expired: Boolean = false,
    val alarmName: String? = null,
    val sound: AlarmMode = AlarmMode.OFF,
    val vibrate: AlarmMode = AlarmMode.OFF,
    val soundUri: String? = null,
    val snoozeTime: Int = 0,
    val color: TaskColorOption = TaskColorOption.DEFAULT,
    val image: TaskImageAttachment? = null,
    val reminders: List<TaskReminderEntry> = emptyList(),
    val locations: List<TaskLocationEntry> = emptyList()
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
    override val snoozeTime: Int,
    val endDate: LocalDateTime? = null,
    val allDay: Boolean = false,
    val note: String? = null,
    val rrule: String? = null,
    val soundUri: String? = null,
    val color: TaskColorOption = TaskColorOption.DEFAULT,
    val image: TaskImageAttachment? = null,
    val reminders: List<TaskReminderEntry> = emptyList(),
    val locations: List<TaskLocationEntry> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),
    val updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
) : SchedulableItem {
    // Removed isExpired - it called LocalDateTime.now() on every access
    // Use date.isBefore(LocalDateTime.now()) directly in UI when needed
    
    val hasAlarm: Boolean
        get() = sound != AlarmMode.OFF || vibrate != AlarmMode.OFF
}
