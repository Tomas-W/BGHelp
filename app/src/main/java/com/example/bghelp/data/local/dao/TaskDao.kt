package com.example.bghelp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.bghelp.data.local.relations.TaskWithRelations
import com.example.bghelp.data.local.entity.TaskEntity
import com.example.bghelp.data.local.entity.TaskLocationEntity
import com.example.bghelp.data.local.entity.TaskReminderEntity
import com.example.bghelp.constants.DatabaseConstants as DB
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TaskDao {
    @Insert
    protected abstract suspend fun insertTaskInternal(task: TaskEntity): Long

    @Update
    protected abstract suspend fun updateTaskInternal(task: TaskEntity)

    @Delete
    protected abstract suspend fun deleteTaskInternal(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertRemindersInternal(reminders: List<TaskReminderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertLocationsInternal(locations: List<TaskLocationEntity>)

    @Query("DELETE FROM ${DB.TASK_REMINDER_TABLE} WHERE taskId = :taskId")
    protected abstract suspend fun deleteRemindersForTask(taskId: Int)

    @Query("DELETE FROM ${DB.TASK_LOCATION_TABLE} WHERE taskId = :taskId")
    protected abstract suspend fun deleteLocationsForTask(taskId: Int)

    @Transaction
    open suspend fun addTask(
        task: TaskEntity,
        reminders: List<TaskReminderEntity>,
        locations: List<TaskLocationEntity>
    ): Int {
        val taskId = insertTaskInternal(task).toInt()
        if (reminders.isNotEmpty()) {
            val reminderEntities = reminders.map { it.copy(id = 0, taskId = taskId) }
            insertRemindersInternal(reminderEntities)
        }
        if (locations.isNotEmpty()) {
            val locationEntities = locations.map { it.copy(id = 0, taskId = taskId) }
            insertLocationsInternal(locationEntities)
        }
        return taskId
    }

    @Transaction
    open suspend fun updateTask(
        task: TaskEntity,
        reminders: List<TaskReminderEntity>,
        locations: List<TaskLocationEntity>
    ) {
        updateTaskInternal(task)
        deleteRemindersForTask(task.id)
        deleteLocationsForTask(task.id)
        if (reminders.isNotEmpty()) {
            val reminderEntities = reminders.map { it.copy(id = 0, taskId = task.id) }
            insertRemindersInternal(reminderEntities)
        }
        if (locations.isNotEmpty()) {
            val locationEntities = locations.map { it.copy(id = 0, taskId = task.id) }
            insertLocationsInternal(locationEntities)
        }
    }

    @Transaction
    open suspend fun deleteTask(task: TaskEntity) {
        deleteTaskInternal(task)
    }

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE}
        WHERE id=:id
        """)
    @Transaction
    abstract fun getTaskById(id: Int) : Flow<TaskWithRelations?>

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE}
        ORDER BY startEpoch
        ASC
        """)
    @Transaction
    abstract fun getAllTasks() : Flow<List<TaskWithRelations>>

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE} 
        WHERE startEpoch BETWEEN :startDate
        AND :endDate
        AND deleted = 0
        ORDER BY startEpoch
        ASC
        """)
    @Transaction
    abstract fun getTasksByDateRange(startDate: Long, endDate: Long) : Flow<List<TaskWithRelations>>

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE}
        WHERE rrule IS NOT NULL
        AND startEpoch <= :endDate
        ORDER BY startEpoch ASC
    """)
    @Transaction
    abstract fun getRecurringTasksUpTo(endDate: Long): Flow<List<TaskWithRelations>>

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE}
        WHERE startEpoch > :currentTime
        ORDER BY startEpoch
        ASC
        LIMIT 1
        """)
    @Transaction
    abstract fun getNextTask(currentTime: Long) : Flow<TaskWithRelations?>

    @Query("SELECT COUNT(*) FROM ${DB.TASK_TABLE} WHERE deleted = 0")
    abstract suspend fun getTaskCount(): Int
}


