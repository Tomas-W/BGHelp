package com.example.bghelp.data.repository

import com.example.bghelp.data.local.TaskEntity
import com.example.bghelp.data.local.TaskDao
import kotlinx.coroutines.flow.Flow


class TaskRepository(private val taskDao: TaskDao) {

    suspend fun addTask(task: TaskEntity)  = taskDao.addTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    fun getTaskById(id: Int): Flow<TaskEntity?> = taskDao.getTaskById(id)

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<TaskEntity>> = taskDao.getTasksByDateRange(startDate, endDate)

    fun getNextTask(currentTime: Long): Flow<TaskEntity?> = taskDao.getNextTask(currentTime)
}