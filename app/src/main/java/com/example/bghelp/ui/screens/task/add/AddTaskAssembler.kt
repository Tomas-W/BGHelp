package com.example.bghelp.ui.screens.task.add

import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.domain.model.TaskImageAttachment
import com.example.bghelp.utils.TaskMapper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object AddTaskAssembler {

    sealed class BuildResult {
        data class Success(val task: CreateTask) : BuildResult()
        data class Error(val message: String) : BuildResult()
    }

    fun buildOrError(
        // Validation inputs
        title: String,
        dateSelection: UserDateSelection,
        startDate: LocalDate,
        startTime: LocalTime,
        repeatSelection: UserRepeatSelection,
        weeklyDays: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        monthlyDays: Set<Int>,
        remindSelection: UserRemindSelection,
        startReminders: List<Reminder>,
        endReminders: List<Reminder>,
        endDate: LocalDate?,
        isEndDateVisible: Boolean,
        endTime: LocalTime?,
        isEndTimeVisible: Boolean,
        // Mapping-only inputs
        rrule: String?,
        alarmName: String?,
        soundMode: AlarmMode,
        vibrateMode: AlarmMode,
        soundUri: String?,
        color: FeatureColor,
        image: TaskImageAttachment?,
        description: String?,
        note: String?,
        locations: List<TaskLocation>
    ): BuildResult {
        // 1) Validate
        AddTaskValidator.validateTitle(title)?.let { return BuildResult.Error(it) }
        AddTaskValidator.validateDateTime(
            dateSelection = dateSelection,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            isEndDateVisible = isEndDateVisible,
            endTime = endTime,
            isEndTimeVisible = isEndTimeVisible
        )?.let { return BuildResult.Error(it) }
        AddTaskValidator.validateRepeat(
            repeatSelection = repeatSelection,
            weeklyDays = weeklyDays,
            monthlyDaySelection = monthlyDaySelection,
            monthlyDays = monthlyDays
        )?.let { return BuildResult.Error(it) }
        AddTaskValidator.validateReminders(
            remindSelection = remindSelection,
            dateSelection = dateSelection,
            startDate = startDate,
            startTime = startTime,
            startReminders = startReminders,
            endReminders = endReminders,
            endDate = endDate,
            isEndDateVisible = isEndDateVisible,
            endTime = endTime,
            isEndTimeVisible = isEndTimeVisible
        )?.let { return BuildResult.Error(it) }

        // 2) Map
        val allDay = dateSelection == UserDateSelection.ON
        val startDateTime = LocalDateTime.of(startDate, if (allDay) LocalTime.MIDNIGHT else startTime)

        val hasEnd = isEndDateVisible || isEndTimeVisible
        val effectiveEndDate = when {
            allDay -> startDate
            hasEnd -> endDate ?: startDate
            else -> null
        }
        val effectiveEndTime = when {
            allDay -> LocalTime.of(23, 59)
            !hasEnd -> null
            endTime != null -> endTime
            else -> startTime
        }
        val endDateTime = if (effectiveEndDate != null && effectiveEndTime != null) {
            LocalDateTime.of(effectiveEndDate, effectiveEndTime)
        } else null

        val taskReminders = if (remindSelection == UserRemindSelection.ON) {
            val start = with(TaskMapper) {
                startReminders.toDomainReminders(com.example.bghelp.domain.model.ReminderKind.START)
            }
            val includeEnd = isEndDateVisible || isEndTimeVisible
            val end = if (includeEnd) {
                with(TaskMapper) {
                    endReminders.toDomainReminders(com.example.bghelp.domain.model.ReminderKind.END)
                }
            } else emptyList()
            start + end
        } else emptyList()

        val taskLocations = with(TaskMapper) { locations.toDomainLocations() }

        val create = CreateTask(
            date = startDateTime,
            endDate = endDateTime,
            allDay = allDay,
            title = title,
            description = description,
            note = note,
            rrule = rrule,
            expired = false,
            alarmName = alarmName,
            sound = soundMode,
            vibrate = vibrateMode,
            soundUri = soundUri,
            snoozeTime = 0,
            color = color,
            image = image,
            reminders = taskReminders,
            locations = taskLocations
        )
        return BuildResult.Success(create)
    }
}


