package com.example.bghelp.data.repository

import com.example.bghelp.data.local.TaskDao
import com.example.bghelp.data.local.TaskEntity
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.Task
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
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
        val entity = createTask.toEntity()
        taskDao.addTask(entity)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override fun getTaskById(id: Int): Flow<Task?> =
        taskDao.getTaskById(id).map { it?.toDomain() }

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<Task>> =
        taskDao.getTasksByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getNextTask(currentTime: Long): Flow<Task?> =
        taskDao.getNextTask(currentTime).map { it?.toDomain() }

    // Entity → Domain
    private fun TaskEntity.toDomain(): Task {
        val localDateTime = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        return Task(
            id = id,
            dateTime = localDateTime,
            message = message,
            expired = expired,
            alarmName = alarmName,
            sound = sound,
            vibrate = vibrate,
            snoozeTime = snoozeTime
        )
    }

    // Domain → Entity
    private fun Task.toEntity(): TaskEntity {
        val epochMillis = dateTime.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return TaskEntity(
            id = id,
            date = epochMillis,
            message = message,
            expired = expired,
            alarmName = alarmName,
            sound = sound,
            vibrate = vibrate,
            snoozeTime = snoozeTime
        )
    }

    // CreateTask → Entity
    private fun CreateTask.toEntity(): TaskEntity {
        val epochMillis = date.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return TaskEntity(
            id = 0, // db will auto-generate
            date = epochMillis,
            message = message,
            expired = expired,
            alarmName = alarmName,
            sound = sound,
            vibrate = vibrate,
            snoozeTime = snoozeTime
        )
    }
}