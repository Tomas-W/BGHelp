package com.example.bghelp.domain.model

import com.example.bghelp.domain.constants.ColorSeeds
import java.time.LocalDateTime
import java.time.ZoneId


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
    val title: String,
    val info: String? = null,
    val allDay: Boolean = false,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime? = null,
    val snoozeSeconds: Int = 0,
    val note: String? = null,
    val rrule: String? = null,
    val expired: Boolean = false,
    val alarmName: String? = null,
    val sound: AlarmMode = AlarmMode.OFF,
    val vibrate: AlarmMode = AlarmMode.OFF,
    val soundUri: String? = null,
    val snoozeValue1: Int,
    val snoozeUnit1: ReminderOffsetUnit,
    val snoozeValue2: Int,
    val snoozeUnit2: ReminderOffsetUnit,
    val color: FeatureColor,
    val image: TaskImageAttachment? = null,
    val reminders: List<TaskReminderEntry> = emptyList(),
    val locations: List<TaskLocationEntry> = emptyList()
)

data class Task(
    val id: Int,
    val date: LocalDateTime,
    val title: String,
    val description: String?,
    val expired: Boolean,
    val alarmName: String?,
    val sound: AlarmMode,
    val vibrate: AlarmMode,
    val snoozeSeconds: Int,
    val snoozeValue1: Int,
    val snoozeUnit1: ReminderOffsetUnit,
    val snoozeValue2: Int,
    val snoozeUnit2: ReminderOffsetUnit,
    val endDate: LocalDateTime? = null,
    val allDay: Boolean = false,
    val note: String? = null,
    val rrule: String? = null,
    val soundUri: String? = null,
    val color: FeatureColor,
    val image: TaskImageAttachment? = null,
    val reminders: List<TaskReminderEntry> = emptyList(),
    val locations: List<TaskLocationEntry> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),
    val updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),
    val deleted: Boolean = false
)

data class ExampleTask(
    val id: Int = 0,
    val date: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),
    val title: String = "This is an example title",
    val description: String? = "This is example info. \n- Example 1\n- Example 2\n- Example 3",
    val expired: Boolean = false,
    val alarmName: String? = "Alarm name",
    val sound: AlarmMode = AlarmMode.ONCE,
    val vibrate: AlarmMode = AlarmMode.CONTINUOUS,
    val snoozeSeconds: Int = 15,
    val snoozeValue1: Int = 10,
    val snoozeUnit1: ReminderOffsetUnit = ReminderOffsetUnit.MINUTES,
    val snoozeValue2: Int = 1,
    val snoozeUnit2: ReminderOffsetUnit = ReminderOffsetUnit.HOURS,
    val endDate: LocalDateTime? = LocalDateTime.now(ZoneId.systemDefault()).plusDays(1),
    val allDay: Boolean = false,
    val note: String? = "Note A",
    val rrule: String? = null,
    val soundUri: String? = null,
    val color: FeatureColor = ColorSeeds.FallbackTaskColor,
    val image: TaskImageAttachment? = null,
    val reminders: List<TaskReminderEntry> = emptyList(),
    val locations: List<TaskLocationEntry> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),
    val updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
) {
    fun toTask(): Task = Task(
        id = id,
        date = date,
        title = title,
        description = description,
        expired = expired,
        alarmName = alarmName,
        sound = sound,
        vibrate = vibrate,
        snoozeSeconds = snoozeSeconds,
        snoozeValue1 = snoozeValue1,
        snoozeUnit1 = snoozeUnit1,
        snoozeValue2 = snoozeValue2,
        snoozeUnit2 = snoozeUnit2,
        endDate = endDate,
        allDay = allDay,
        note = note,
        rrule = rrule,
        soundUri = soundUri,
        color = color,
        image = image,
        reminders = reminders,
        locations = locations,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deleted = false
    )
}
