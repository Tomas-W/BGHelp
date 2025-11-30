package com.example.bghelp.ui.screens.task.add

import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.TaskImageAttachment
import com.example.bghelp.utils.TimeUnitMapper
import java.time.LocalDateTime
import java.time.LocalTime

object AddTaskAssembler {

    sealed class BuildResult {
        data class Success(val task: CreateTask) : BuildResult()
        data class Error(val message: String) : BuildResult()
    }

    fun buildOrError(
        formState: AddTaskFormState,
        color: FeatureColor,
        image: TaskImageAttachment?
    ): BuildResult {
        // Title
        val title = formState.title
        val info = formState.info.takeIf { it.isNotBlank() }
        // Date
        val allDay = formState.dateSelection == UserDateSelection.ON
        val startDateTime = LocalDateTime.of(
            formState.startDate,
            if (allDay) LocalTime.MIDNIGHT else formState.startTime
        )
        val hasEnd = formState.isEndDateVisible || formState.isEndTimeVisible
        val effectiveEndDate = when {
            allDay -> formState.startDate
            hasEnd -> formState.endDate ?: formState.startDate
            else -> null
        }
        val effectiveEndTime = when {
            allDay -> LocalTime.of(23, 59)
            !hasEnd -> null
            formState.endTime != null -> formState.endTime
            else -> formState.startTime
        }
        val endDateTime = if (effectiveEndDate != null && effectiveEndTime != null) {
            LocalDateTime.of(effectiveEndDate, effectiveEndTime)
        } else null
        val expired = false

        // Repeat
        val rrule = formState.repeatRRule

        // Remind
        val taskReminders = if (formState.remindSelection == UserRemindSelection.ON) {
            val start = with(TaskMapper) {
                formState.startReminders.toDomainReminders(ReminderKind.START)
            }
            val includeEnd = formState.isEndDateVisible || formState.isEndTimeVisible
            val end = if (includeEnd) {
                with(TaskMapper) {
                    formState.endReminders.toDomainReminders(ReminderKind.END)
                }
            } else emptyList()
            start + end
        } else emptyList()
        val soundMode = with(TaskMapper) { formState.soundSelection.toAlarmMode() }
        val vibrateMode = with(TaskMapper) { formState.vibrateSelection.toAlarmMode() }
        val alarmName = formState.selectedAudioFile.takeIf { it.isNotBlank() }
        val soundUri = formState.selectedAudioFile.takeIf { it.isNotBlank() }
        val snoozeSeconds = 0
        val snoozeUnit1Domain = with(TimeUnitMapper) {
            formState.snoozeUnit1.toReminderOffsetUnit()
        }
        val snoozeValue1 = formState.snoozeValue1
        val snoozeUnit2Domain = with(TimeUnitMapper) {
            formState.snoozeUnit2.toReminderOffsetUnit()
        }
        val snoozeValue2 = formState.snoozeValue2

        // Note
        val note = if (formState.noteSelection == UserNoteSelection.ON)
            formState.note.takeIf { it.isNotBlank() } else null

        // Location
        val taskLocations = with(TaskMapper) { formState.selectedLocations.toDomainLocations() }

        val create = CreateTask(
            title = title,
            info = info,

            allDay = allDay,
            startDate = startDateTime,
            endDate = endDateTime,
            expired = expired,

            rrule = rrule,

            reminders = taskReminders,
            sound = soundMode,
            alarmName = alarmName,
            soundUri = soundUri,
            vibrate = vibrateMode,
            snoozeSeconds = snoozeSeconds,
            snoozeValue1 = snoozeValue1,
            snoozeUnit1 = snoozeUnit1Domain,
            snoozeValue2 = snoozeValue2,
            snoozeUnit2 = snoozeUnit2Domain,

            note = note,
            locations = taskLocations,
            image = image,
            color = color,
        )
        return BuildResult.Success(create)
    }
}
