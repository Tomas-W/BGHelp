package com.example.bghelp.utils

import com.example.bghelp.data.local.dao.ColorDao
import com.example.bghelp.data.local.entity.ColorEntity
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.domain.constants.ColorSeeds
import com.example.bghelp.domain.constants.TaskSeeds
import com.example.bghelp.domain.model.CreateFeatureColor
import com.example.bghelp.domain.model.FeatureColor
import android.util.Log
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val colorDao: ColorDao,
    private val taskRepository: TaskRepository
) {
    suspend fun initialize() {
        try {
            Log.d("DatabaseInitializer", "Starting database initialization...")
            
            // Initialize colors first
            initializeDefaultColors(colorDao)
            
            // Initialize tasks after colors are available
            initializeDefaultTasks(taskRepository, colorDao)
            
            Log.d("DatabaseInitializer", "Database initialization completed successfully")
        } catch (e: Exception) {
            Log.e("DatabaseInitializer", "Failed to initialize database", e)
            // Don't rethrow - allow app to continue even if initialization fails
            // The app can still function, just without default data
        }
    }
}

/**
 * Initializes default colors in the database if they don't already exist.
 * Checks for existing default colors before inserting to avoid duplicates.
 */
suspend fun initializeDefaultColors(colorDao: ColorDao) {
    val existingColors = colorDao.getDefaultColors().first()
    
    if (existingColors.isEmpty()) {
        Log.d("DatabaseInitializer", "No default colors found, initializing...")
        ColorSeeds.DefaultColors.forEach { createColor ->
            val entity = createColor.toEntity()
            colorDao.insertColor(entity)
        }
        Log.d("DatabaseInitializer", "Default colors initialized successfully")
    } else {
        Log.d("DatabaseInitializer", "Default colors already exist, skipping initialization")
    }
}

/**
 * Initializes default tasks in the database if they don't already exist.
 * Requires colors to be initialized first.
 */
suspend fun initializeDefaultTasks(
    taskRepository: TaskRepository,
    colorDao: ColorDao
) {
    // Get available colors for task creation
    val availableColors = colorDao.getDefaultColors().first()
        .map { entity ->
            FeatureColor(
                id = entity.id,
                name = entity.name,
                red = entity.red,
                green = entity.green,
                blue = entity.blue,
                alpha = entity.alpha
            )
        }
    
    if (availableColors.isEmpty()) {
        Log.w("DatabaseInitializer", "No colors available for task initialization")
        return
    }
    
    // Check if tasks already exist using a simple count query
    val taskCount = taskRepository.getTaskCount()
    
    if (taskCount == 0) {
        Log.d("DatabaseInitializer", "No tasks found, initializing default tasks...")
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val defaultTasks = TaskSeeds.generateDefaultTasks(availableColors, now)
        defaultTasks.forEach { createTask ->
            taskRepository.addTask(createTask)
        }
        Log.d("DatabaseInitializer", "Default tasks initialized successfully")
    } else {
        Log.d("DatabaseInitializer", "Tasks already exist, skipping initialization")
    }
}

/**
 * Converts CreateFeatureColor to ColorEntity for database insertion.
 */
private fun CreateFeatureColor.toEntity(): ColorEntity {
    return ColorEntity(
        id = 0, // Will be auto-generated
        name = name,
        red = red,
        green = green,
        blue = blue,
        alpha = alpha,
        isDefault = isDefault
    )
}
