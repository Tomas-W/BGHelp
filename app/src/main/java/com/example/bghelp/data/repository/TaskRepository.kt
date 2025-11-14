package com.example.bghelp.data.repository

import com.example.bghelp.data.local.TaskDao
import com.example.bghelp.data.local.TaskEntity
import com.example.bghelp.data.local.TaskLocationEntity
import com.example.bghelp.data.local.TaskReminderEntity
import com.example.bghelp.data.local.TaskWithRelations
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.Task
import com.example.bghelp.domain.model.TaskImageAttachment
import com.example.bghelp.domain.model.TaskLocationEntry
import com.example.bghelp.domain.model.TaskReminderEntry
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface TaskRepository {
    suspend fun addTask(createTask: CreateTask)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    fun getTaskById(id: Int): Flow<Task?>
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<Task>>
    fun getNextTask(currentTime: Long): Flow<Task?>
}

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {
    override suspend fun addTask(createTask: CreateTask) {
        val persistence = createTask.toPersistence()
        taskDao.addTask(
            task = persistence.task,
            reminders = persistence.reminders,
            locations = persistence.locations
        )
    }

    override suspend fun updateTask(task: Task) {
        val persistence = task.toPersistence()
        taskDao.updateTask(
            task = persistence.task,
            reminders = persistence.reminders,
            locations = persistence.locations
        )
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntityOnly())
    }

    override fun getTaskById(id: Int): Flow<Task?> =
        taskDao.getTaskById(id).map { it?.toDomain() }

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
                .filter { task ->
                    // If it's a recurring task, check if its date matches the pattern
                    if (task.rrule != null) {
                        val rule = parseRRule(task.rrule) ?: return@filter true
                        occursOnDate(task, rule, task.date.toLocalDate())
                    } else {
                        true // Non-recurring tasks always included
                    }
                }
        }

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
                        val rule = parseRRule(task.rrule) ?: return@filter true
                        occursOnDate(task, rule, task.date.toLocalDate())
                    } else {
                        true // Non-recurring tasks always included
                    }
                }
            val baseOccurrences = baseDomain.map { it.id to it.date }.toSet()

            val additional = recurringTasks.flatMap { relation ->
                val task = relation.toDomain()
                val rule = task.rrule ?: return@flatMap emptyList<Task>()
                val occurrences = generateOccurrences(task, rule, windowStart, windowEndExclusive)
                occurrences.filter { occurrence ->
                    (occurrence.id to occurrence.date) !in baseOccurrences
                }
            }

            (baseDomain + additional).sortedBy { it.date }
        }
    }

    override fun getNextTask(currentTime: Long): Flow<Task?> =
        taskDao.getNextTask(currentTime).map { it?.toDomain() }

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
            endDate = endDateTime,
            allDay = task.allDay,
            note = task.note,
            rrule = task.rrule,
            soundUri = task.soundUri,
            color = task.color,
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
    private fun Task.toEntityOnly(updatedAtMillis: Long = System.currentTimeMillis()): TaskEntity =
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
            color = color,
            imageUri = image?.uri,
            imageName = image?.displayName,
            imageSource = image?.source,
            createdAt = createdAt.toEpochMillis(),
            updatedAt = updatedAtMillis
        )

    // Domain → Persistence bundle
    private fun Task.toPersistence(): TaskPersistenceModel {
        val updatedAtMillis = System.currentTimeMillis()
        val entity = toEntityOnly(updatedAtMillis)
        val reminderEntities = reminders.map { it.toEntity() }
        val locationEntities = locations.map { it.toEntity() }
        return TaskPersistenceModel(
            task = entity,
            reminders = reminderEntities,
            locations = locationEntities
        )
    }

    private fun CreateTask.toPersistence(): TaskPersistenceModel {
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
            color = color,
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

    private fun generateOccurrences(
        task: Task,
        ruleString: String,
        windowStart: LocalDateTime,
        windowEndExclusive: LocalDateTime
    ): List<Task> {
        val rule = parseRRule(ruleString) ?: return emptyList()
        val occurrences = mutableListOf<Task>()
        val lastDate = windowEndExclusive.minusNanos(1).toLocalDate()
        var currentDate = windowStart.toLocalDate()

        while (!currentDate.isAfter(lastDate)) {
            if (occursOnDate(task, rule, currentDate)) {
                val occurrenceStart = LocalDateTime.of(currentDate, task.date.toLocalTime())
                if (!occurrenceStart.isBefore(windowStart) && occurrenceStart.isBefore(windowEndExclusive)) {
                    occurrences.add(task.createOccurrence(occurrenceStart))
                }
            }
            currentDate = currentDate.plusDays(1)
        }

        return occurrences
    }

    private fun occursOnDate(task: Task, rule: RecurrenceRule, date: LocalDate): Boolean {
        // Check if date is after UNTIL date
        if (rule.until != null && date.isAfter(rule.until)) {
            return false
        }
        
        return when (rule.frequency) {
            Frequency.WEEKLY -> occursWeekly(task, rule, date)
            Frequency.MONTHLY -> occursMonthly(task, rule, date)
        }
    }

    private fun occursWeekly(task: Task, rule: RecurrenceRule, date: LocalDate): Boolean {
        val baseDate = task.date.toLocalDate()
        if (date.isBefore(baseDate)) return false

        val byDays = if (rule.byDay.isEmpty()) {
            setOf(task.date.dayOfWeek)
        } else {
            rule.byDay
        }
        if (date.dayOfWeek !in byDays) return false

        val daysBetween = ChronoUnit.DAYS.between(baseDate, date)
        if (daysBetween < 0) return false

        val weeksBetween = daysBetween / 7
        return weeksBetween % rule.interval == 0L
    }

    private fun occursMonthly(task: Task, rule: RecurrenceRule, date: LocalDate): Boolean {
        val baseDate = task.date.toLocalDate()
        if (date.isBefore(baseDate)) return false

        val monthsBetween = ChronoUnit.MONTHS.between(
            YearMonth.from(baseDate),
            YearMonth.from(date)
        )
        if (monthsBetween < 0) return false
        if (monthsBetween % rule.interval != 0L) return false

        if (rule.byMonth.isNotEmpty() && date.monthValue !in rule.byMonth) return false

        val dayCandidates = if (rule.byMonthDay.isEmpty()) {
            listOf(baseDate.dayOfMonth)
        } else {
            rule.byMonthDay
        }

        val matchesDay = dayCandidates.any { day ->
            when {
                day == -1 -> date == date.with(TemporalAdjusters.lastDayOfMonth())
                day > 0 -> date.dayOfMonth == day
                else -> false
            }
        }

        return matchesDay
    }

    private fun Task.createOccurrence(start: LocalDateTime): Task {
        val duration = endDate?.let { Duration.between(date, it) }
        val newEnd = duration?.let { start.plus(it) }
        val now = LocalDateTime.now()
        val isExpired = if (allDay && newEnd != null) {
            newEnd.isBefore(now)
        } else {
            start.isBefore(now)
        }
        return copy(
            date = start,
            endDate = newEnd,
            expired = isExpired
        )
    }

    private fun parseRRule(rrule: String): RecurrenceRule? {
        if (rrule.isBlank()) return null

        val components = rrule.split(";")
        val values = buildMap {
            components.forEach { component ->
                val separatorIndex = component.indexOf('=')
                if (separatorIndex > 0) {
                    val key = component.substring(0, separatorIndex).uppercase()
                    val value = component.substring(separatorIndex + 1)
                    put(key, value)
                }
            }
        }

        val frequency = when (values["FREQ"]?.uppercase()) {
            "WEEKLY" -> Frequency.WEEKLY
            "MONTHLY" -> Frequency.MONTHLY
            else -> return null
        }

        val interval = values["INTERVAL"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
        
        val until = values["UNTIL"]?.let { untilStr ->
            parseUntilDate(untilStr)
        }

        return when (frequency) {
            Frequency.WEEKLY -> {
                val byDay = values["BYDAY"]
                    ?.split(",")
                    ?.mapNotNull { parseDayOfWeek(it) }
                    ?.toSet()
                    ?: emptySet()
                RecurrenceRule(
                    frequency = frequency,
                    interval = interval,
                    byDay = byDay,
                    until = until
                )
            }

            Frequency.MONTHLY -> {
                val byMonth = values["BYMONTH"]
                    ?.takeIf { it.isNotBlank() }
                    ?.split(",")
                    ?.mapNotNull { it.toIntOrNull() }
                    ?.toSet()
                    ?: emptySet()

                val byMonthDay = values["BYMONTHDAY"]
                    ?.takeIf { it.isNotBlank() }
                    ?.split(",")
                    ?.mapNotNull { it.toIntOrNull() }
                    ?: emptyList()

                RecurrenceRule(
                    frequency = frequency,
                    interval = interval,
                    byMonth = byMonth,
                    byMonthDay = byMonthDay,
                    until = until
                )
            }
        }
    }

    private fun parseDayOfWeek(token: String): DayOfWeek? =
        when (token.uppercase()) {
            "MO" -> DayOfWeek.MONDAY
            "TU" -> DayOfWeek.TUESDAY
            "WE" -> DayOfWeek.WEDNESDAY
            "TH" -> DayOfWeek.THURSDAY
            "FR" -> DayOfWeek.FRIDAY
            "SA" -> DayOfWeek.SATURDAY
            "SU" -> DayOfWeek.SUNDAY
            else -> null
        }

    private fun parseUntilDate(untilStr: String): LocalDate? {
        // Parse YYYYMMDD format
        if (untilStr.length == 8) {
            val year = untilStr.substring(0, 4).toIntOrNull()
            val month = untilStr.substring(4, 6).toIntOrNull()
            val day = untilStr.substring(6, 8).toIntOrNull()
            if (year != null && month != null && day != null) {
                return try {
                    LocalDate.of(year, month, day)
                } catch (e: Exception) {
                    null
                }
            }
        }
        return null
    }

    private data class RecurrenceRule(
        val frequency: Frequency,
        val interval: Int,
        val byDay: Set<DayOfWeek> = emptySet(),
        val byMonth: Set<Int> = emptySet(),
        val byMonthDay: List<Int> = emptyList(),
        val until: LocalDate? = null
    )

    private enum class Frequency {
        WEEKLY,
        MONTHLY
    }

    private data class TaskPersistenceModel(
        val task: TaskEntity,
        val reminders: List<TaskReminderEntity>,
        val locations: List<TaskLocationEntity>
    )
}
