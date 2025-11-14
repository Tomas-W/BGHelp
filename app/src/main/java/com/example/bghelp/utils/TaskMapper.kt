package com.example.bghelp.utils

import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.ReminderOffsetUnit
import com.example.bghelp.domain.model.TaskColorOption
import com.example.bghelp.domain.model.TaskLocationEntry
import com.example.bghelp.domain.model.TaskReminderEntry
import com.example.bghelp.ui.screens.task.add.Reminder
import com.example.bghelp.ui.screens.task.add.TaskLocation
import com.example.bghelp.ui.screens.task.add.TimeUnit
import com.example.bghelp.ui.screens.task.add.UserColorChoices
import com.example.bghelp.ui.screens.task.add.UserSoundSelection
import com.example.bghelp.ui.screens.task.add.UserVibrateSelection

object TaskMapper {
    fun Reminder.toDomain(type: ReminderKind): TaskReminderEntry? {
        val unit = timeUnit.toDomainUnit() ?: return null
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

    fun UserColorChoices.toDomainColor(): TaskColorOption = when (this) {
        UserColorChoices.DEFAULT -> TaskColorOption.DEFAULT
        UserColorChoices.RED -> TaskColorOption.RED
        UserColorChoices.GREEN -> TaskColorOption.GREEN
        UserColorChoices.YELLOW -> TaskColorOption.YELLOW
        UserColorChoices.CYAN -> TaskColorOption.CYAN
        UserColorChoices.MAGENTA -> TaskColorOption.MAGENTA
    }

    private fun TimeUnit.toDomainUnit(): ReminderOffsetUnit? = when (this) {
        TimeUnit.MINUTES -> ReminderOffsetUnit.MINUTES
        TimeUnit.HOURS -> ReminderOffsetUnit.HOURS
        TimeUnit.DAYS -> ReminderOffsetUnit.DAYS
        TimeUnit.WEEKS -> ReminderOffsetUnit.WEEKS
        TimeUnit.MONTHS -> ReminderOffsetUnit.MONTHS
    }
}

