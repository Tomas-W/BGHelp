package com.example.bghelp.utils

import com.example.bghelp.data.local.dao.ColorDao
import com.example.bghelp.data.local.entity.ColorEntity
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.domain.constants.ColorSeeds
import com.example.bghelp.domain.constants.TaskSeeds
import com.example.bghelp.domain.model.FeatureColor
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId

suspend fun initializeDefaultColors(colorDao: ColorDao) {
    val existingColors = colorDao.getAllColors().first()
    if (existingColors.isNotEmpty()) {
        return
    }

    val defaultColors = ColorSeeds.DefaultColors.map {
        ColorEntity(
            name = it.name,
            red = it.red,
            green = it.green,
            blue = it.blue,
            alpha = it.alpha,
            isDefault = it.isDefault
        )
    }

    defaultColors.forEach { colorDao.insertColor(it) }
}

suspend fun initializeDefaultTasks(taskRepository: TaskRepository, colorDao: ColorDao) {
    val existingTasks = taskRepository.getTasksByDateRange(0, Long.MAX_VALUE).first()
    if (existingTasks.isNotEmpty()) {
        return
    }

    val colors = colorDao.getAllColors().first().map {
        FeatureColor(
            id = it.id,
            name = it.name,
            red = it.red,
            green = it.green,
            blue = it.blue,
            alpha = it.alpha
        )
    }

    val now = LocalDateTime.now(ZoneId.systemDefault())
    val defaultTasks = TaskSeeds.generateDefaultTasks(colors, now)

    defaultTasks.forEach { taskRepository.addTask(it) }
}

