package com.example.bghelp.data.repository

import com.example.bghelp.data.local.dao.TaskDao
import com.example.bghelp.data.local.entity.TaskEntity
import com.example.bghelp.data.local.entity.TaskLocationEntity
import com.example.bghelp.data.local.entity.TaskReminderEntity
import com.example.bghelp.data.local.relations.TaskWithRelations
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.Task
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.domain.model.TaskImageAttachment
import com.example.bghelp.domain.constants.ColorSeeds
import com.example.bghelp.domain.service.RecurrenceCalculator
import com.example.bghelp.domain.model.TaskLocationEntry
import com.example.bghelp.domain.model.TaskReminderEntry
import com.example.bghelp.ui.theme.TaskDefault
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface TaskRepository {
    suspend fun addTask(createTask: CreateTask)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    fun getTaskById(id: Int): Flow<Task?>
    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<Task>>
}

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {
    override suspend fun addTask(createTask: CreateTask) {
        val persistence = createTask.toPersistenceBundle()
        taskDao.addTask(
            task = persistence.task,
            reminders = persistence.reminders,
            locations = persistence.locations
        )
    }

    override suspend fun updateTask(task: Task) {
        val persistence = task.toPersistenceBundle()
        taskDao.updateTask(
            task = persistence.task,
            reminders = persistence.reminders,
            locations = persistence.locations
        )
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override fun getTaskById(id: Int): Flow<Task?> =
        taskDao.getTaskById(id).map { it?.toDomain() }

    override fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<Task>> {
        val zone = ZoneId.systemDefault()
        val windowStart = Instant.ofEpochMilli(startDate)
            .atZone(zone)
            .toLocalDateTime()
        val windowEndExclusive = Instant.ofEpochMilli(endDate + 1)
            .atZone(zone)
            .toLocalDateTime()

        val baseFlow = taskDao.getTasksByDateRange(startDate, endDate)
        val recurringFlow = taskDao.getRecurringTasksUpTo(endDate)

        return combine(baseFlow, recurringFlow) { baseTasks, recurringTasks ->
            val baseDomain = baseTasks.map { it.toDomain() }
                .filter { task ->
                    // If it's a recurring task, check if its date matches the pattern
                    if (task.rrule != null) {
                        val rule = RecurrenceCalculator.parseRRule(task.rrule) ?: return@filter true
                        RecurrenceCalculator.occursOnDate(task, rule, task.date.toLocalDate())
                    } else {
                        true // Non-recurring tasks always included
                    }
                }
            val baseOccurrences = baseDomain.map { it.id to it.date }.toSet()

            val additional = recurringTasks.flatMap { relation ->
                val task = relation.toDomain()
                val rule = task.rrule ?: return@flatMap emptyList<Task>()
                val occurrences = RecurrenceCalculator.generateOccurrences(task, rule, windowStart, windowEndExclusive)
                occurrences.filter { occurrence ->
                    (occurrence.id to occurrence.date) !in baseOccurrences
                }
            }

            (baseDomain + additional).sortedBy { it.date }
        }
    }

    // Entity → Domain
    private fun TaskWithRelations.toDomain(): Task {
        val zone = ZoneId.systemDefault()
        val startDateTime = Instant.ofEpochMilli(task.startEpoch)
            .atZone(zone)
            .toLocalDateTime()
        val endDateTime = task.endEpoch?.let {
            Instant.ofEpochMilli(it).atZone(zone).toLocalDateTime()
        }
        val createdAt = Instant.ofEpochMilli(task.createdAt)
            .atZone(zone)
            .toLocalDateTime()
        val updatedAt = Instant.ofEpochMilli(task.updatedAt)
            .atZone(zone)
            .toLocalDateTime()

        // Recalculate expiration for all tasks
        val now = LocalDateTime.now()
        val isExpired = if (task.allDay && endDateTime != null) {
            // All-day task - check endDate
            endDateTime.isBefore(now)
        } else {
            // Regular task - check start date
            startDateTime.isBefore(now)
        }

        val taskColor = color?.let {
            FeatureColor(
                id = it.id,
                name = it.name,
                red = it.red,
                green = it.green,
                blue = it.blue,
                alpha = it.alpha
            )
        } ?: ColorSeeds.FallbackTaskColor

        return Task(
            id = task.id,
            date = startDateTime,
            title = task.title,
            description = task.info,
            expired = isExpired,
            alarmName = task.alarmName,
            sound = task.sound,
            vibrate = task.vibrate,
            snoozeTime = task.snoozeMinutes,
            snoozeValue1 = task.snoozeValue1,
            snoozeUnit1 = task.snoozeUnit1,
            snoozeValue2 = task.snoozeValue2,
            snoozeUnit2 = task.snoozeUnit2,
            endDate = endDateTime,
            allDay = task.allDay,
            note = task.note,
            rrule = task.rrule,
            soundUri = task.soundUri,
            color = taskColor,
            image = task.toDomainImage(),
            reminders = reminders.map { it.toDomain() },
            locations = locations
                .sortedBy { it.orderIndex }
                .map { it.toDomain() },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun TaskEntity.toDomainImage(): TaskImageAttachment? {
        if (imageUri == null && imageName == null && imageSource == null) {
            return null
        }
        val source = imageSource ?: return null
        return TaskImageAttachment(
            uri = imageUri,
            displayName = imageName,
            source = source
        )
    }

    // Domain → Entity for updates (task row only)
    private fun Task.toEntity(updatedAtMillis: Long = System.currentTimeMillis()): TaskEntity =
        TaskEntity(
            id = id,
            title = title,
            info = description,
            note = note,
            startEpoch = date.toEpochMillis(),
            endEpoch = endDate?.toEpochMillis(),
            allDay = allDay,
            rrule = rrule,
            expired = expired,
            alarmName = alarmName,
            sound = sound,
            vibrate = vibrate,
            soundUri = soundUri,
            snoozeMinutes = snoozeTime,
            snoozeValue1 = snoozeValue1,
            snoozeUnit1 = snoozeUnit1,
            snoozeValue2 = snoozeValue2,
            snoozeUnit2 = snoozeUnit2,
            colorId = color.id,
            imageUri = image?.uri,
            imageName = image?.displayName,
            imageSource = image?.source,
            createdAt = createdAt.toEpochMillis(),
            updatedAt = updatedAtMillis
        )

    // Domain → Persistence bundle
    private fun Task.toPersistenceBundle(): TaskPersistenceModel {
        val updatedAtMillis = System.currentTimeMillis()
        val entity = toEntity(updatedAtMillis)
        val reminderEntities = reminders.map { it.toEntity() }
        val locationEntities = locations.map { it.toEntity() }
        return TaskPersistenceModel(
            task = entity,
            reminders = reminderEntities,
            locations = locationEntities
        )
    }

    private fun CreateTask.toPersistenceBundle(): TaskPersistenceModel {
        val now = System.currentTimeMillis()
        val entity = TaskEntity(
            id = 0,
            title = title,
            info = description,
            note = note,
            startEpoch = date.toEpochMillis(),
            endEpoch = endDate?.toEpochMillis(),
            allDay = allDay,
            rrule = rrule,
            expired = expired,
            alarmName = alarmName,
            sound = sound,
            vibrate = vibrate,
            soundUri = soundUri,
            snoozeMinutes = snoozeTime,
            snoozeValue1 = snoozeValue1,
            snoozeUnit1 = snoozeUnit1,
            snoozeValue2 = snoozeValue2,
            snoozeUnit2 = snoozeUnit2,
            colorId = color.id,
            imageUri = image?.uri,
            imageName = image?.displayName,
            imageSource = image?.source,
            createdAt = now,
            updatedAt = now
        )
        val reminderEntities = reminders.map { it.toEntity() }
        val locationEntities = locations.map { it.toEntity() }
        return TaskPersistenceModel(
            task = entity,
            reminders = reminderEntities,
            locations = locationEntities
        )
    }

    private fun TaskReminderEntity.toDomain(): TaskReminderEntry =
        TaskReminderEntry(
            id = id,
            type = type,
            offsetValue = offsetValue,
            offsetUnit = offsetUnit
        )

    private fun TaskLocationEntity.toDomain(): TaskLocationEntry =
        TaskLocationEntry(
            id = id,
            latitude = latitude,
            longitude = longitude,
            address = address,
            name = name,
            order = orderIndex
        )

    private fun TaskReminderEntry.toEntity(): TaskReminderEntity =
        TaskReminderEntity(
            id = 0,
            taskId = 0,
            type = type,
            offsetValue = offsetValue,
            offsetUnit = offsetUnit
        )

    private fun TaskLocationEntry.toEntity(): TaskLocationEntity =
        TaskLocationEntity(
            id = 0,
            taskId = 0,
            latitude = latitude,
            longitude = longitude,
            address = address,
            name = name,
            orderIndex = order
        )

    private fun LocalDateTime.toEpochMillis(): Long =
        this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private data class TaskPersistenceModel(
        val task: TaskEntity,
        val reminders: List<TaskReminderEntity>,
        val locations: List<TaskLocationEntity>
    )
}
