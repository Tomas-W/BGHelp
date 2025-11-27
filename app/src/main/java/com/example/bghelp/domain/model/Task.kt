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
    val title: String,
    val info: String?,
    val allDay: Boolean = false,
    val date: LocalDateTime,
    val endDate: LocalDateTime? = null,
    val expired: Boolean,
    val deleted: Boolean = false,
    val rrule: String? = null,
    val reminders: List<TaskReminderEntry> = emptyList(),
    val sound: AlarmMode,
    val alarmName: String?,
    val soundUri: String? = null,
    val vibrate: AlarmMode,
    val snoozeSeconds: Int,
    val snoozeValue1: Int,
    val snoozeUnit1: ReminderOffsetUnit,
    val snoozeValue2: Int,
    val snoozeUnit2: ReminderOffsetUnit,
    val note: String? = null,
    val locations: List<TaskLocationEntry> = emptyList(),
    val image: TaskImageAttachment? = null,
    val color: FeatureColor,
    val createdAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),
    val updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),

)

data class ExampleTask(
    val id: Int = 0,
    val title: String = "This is an example title",
    val info: String? = "This is example info. \n- Example 1\n- Example 2\n- Example 3",
    val allDay: Boolean = false,
    val date: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),
    val endDate: LocalDateTime? = LocalDateTime.now(ZoneId.systemDefault()).plusDays(1),
    val expired: Boolean = false,
    val rrule: String? = null,
    val reminders: List<TaskReminderEntry> = emptyList(),
    val sound: AlarmMode = AlarmMode.ONCE,
    val alarmName: String? = "Alarm name",
    val soundUri: String? = null,
    val vibrate: AlarmMode = AlarmMode.CONTINUOUS,
    val snoozeSeconds: Int = 15,
    val snoozeValue1: Int = 10,
    val snoozeUnit1: ReminderOffsetUnit = ReminderOffsetUnit.MINUTES,
    val snoozeValue2: Int = 1,
    val snoozeUnit2: ReminderOffsetUnit = ReminderOffsetUnit.HOURS,
    val note: String? = "Note A",
    val locations: List<TaskLocationEntry> = emptyList(),
    val image: TaskImageAttachment? = null,
    val color: FeatureColor = ColorSeeds.FallbackTaskColor,
    val createdAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()),
    val updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
) {
    fun toTask(): Task = Task(
        id = id,
        title = title,
        info = info,
        allDay = allDay,
        date = date,
        endDate = endDate,
        expired = expired,
        deleted = false,
        rrule = rrule,
        reminders = reminders,
        sound = sound,
        alarmName = alarmName,
        soundUri = soundUri,
        vibrate = vibrate,
        snoozeSeconds = snoozeSeconds,
        snoozeValue1 = snoozeValue1,
        snoozeUnit1 = snoozeUnit1,
        snoozeValue2 = snoozeValue2,
        snoozeUnit2 = snoozeUnit2,
        note = note,
        locations = locations,
        image = image,
        color = color,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
