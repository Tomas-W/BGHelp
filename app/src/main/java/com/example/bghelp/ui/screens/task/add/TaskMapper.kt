package com.example.bghelp.ui.screens.task.add

import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.TaskLocationEntry
import com.example.bghelp.domain.model.TaskReminderEntry
import com.example.bghelp.utils.TimeUnitMapper

object TaskMapper {
    fun Reminder.toDomain(type: ReminderKind): TaskReminderEntry? {
        val unit = with(TimeUnitMapper) { timeUnit.toReminderOffsetUnit() }
        return TaskReminderEntry(
            type = type,
            offsetValue = value,
            offsetUnit = unit
        )
    }

    fun List<Reminder>.toDomainReminders(type: ReminderKind): List<TaskReminderEntry> {
        return mapNotNull { it.toDomain(type) }
    }

    fun List<TaskLocation>.toDomainLocations(): List<TaskLocationEntry> {
        return mapIndexed { index, location ->
            TaskLocationEntry(
                latitude = location.latitude,
                longitude = location.longitude,
                address = location.address,
                name = location.name,
                order = index
            )
        }
    }

    fun UserSoundSelection.toAlarmMode(): AlarmMode = when (this) {
        UserSoundSelection.OFF -> AlarmMode.OFF
        UserSoundSelection.ONCE -> AlarmMode.ONCE
        UserSoundSelection.CONTINUOUS -> AlarmMode.CONTINUOUS
    }

    fun UserVibrateSelection.toAlarmMode(): AlarmMode = when (this) {
        UserVibrateSelection.OFF -> AlarmMode.OFF
        UserVibrateSelection.ONCE -> AlarmMode.ONCE
        UserVibrateSelection.CONTINUOUS -> AlarmMode.CONTINUOUS
    }
}

