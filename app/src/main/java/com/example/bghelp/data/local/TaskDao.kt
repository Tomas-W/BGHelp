package com.example.bghelp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bghelp.constants.DatabaseConstants as DB
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TaskDao {
    @Insert
    abstract suspend fun addTask(task: TaskEntity)

    @Update
    abstract suspend fun updateTask(task: TaskEntity)

    @Delete
    abstract suspend fun deleteTask(task: TaskEntity)

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE}
        WHERE id=:id
        """)
    abstract fun getTaskById(id: Int) : Flow<TaskEntity?>

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE}
        ORDER BY date
        ASC
        """)
    abstract fun getAllTasks() : Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE} 
        WHERE date BETWEEN :startDate
        AND :endDate
        ORDER BY date
        ASC
        """)
    abstract fun getTasksByDateRange(startDate: Long, endDate: Long) : Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM ${DB.TASK_TABLE}
        WHERE date > :currentTime
        ORDER BY date
        ASC
        LIMIT 1
        """)
    abstract fun getNextTask(currentTime: Long) : Flow<TaskEntity?>
}
