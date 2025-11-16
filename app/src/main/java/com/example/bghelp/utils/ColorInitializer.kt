package com.example.bghelp.utils

import com.example.bghelp.data.local.dao.ColorDao
import com.example.bghelp.data.local.entity.ColorEntity
import com.example.bghelp.domain.constants.ColorSeeds
import kotlinx.coroutines.flow.first

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

