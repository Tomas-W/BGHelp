package com.example.bghelp.data.repository

import com.example.bghelp.data.local.dao.ColorDao
import com.example.bghelp.data.local.entity.ColorEntity
import com.example.bghelp.domain.model.CreateFeatureColor
import com.example.bghelp.domain.model.FeatureColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ColorRepository {
    fun getAllColors(): Flow<List<FeatureColor>>
    suspend fun getColorById(id: Int): FeatureColor?
    suspend fun addColor(color: CreateFeatureColor): Long
    suspend fun updateColor(color: FeatureColor)
    suspend fun deleteColor(color: FeatureColor)
    fun getDefaultColors(): Flow<List<FeatureColor>>
}

class ColorRepositoryImpl(private val colorDao: ColorDao) : ColorRepository {
    override fun getAllColors(): Flow<List<FeatureColor>> =
        colorDao.getAllColors().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getColorById(id: Int): FeatureColor? =
        colorDao.getColorById(id)?.toDomain()

    override suspend fun addColor(color: CreateFeatureColor): Long {
        return colorDao.insertColor(color.toEntity())
    }

    override suspend fun updateColor(color: FeatureColor) {
        colorDao.updateColor(color.toEntity())
    }

    override suspend fun deleteColor(color: FeatureColor) {
        colorDao.deleteColor(color.toEntity())
    }

    override fun getDefaultColors(): Flow<List<FeatureColor>> =
        colorDao.getDefaultColors().map { entities ->
            entities.map { it.toDomain() }
        }

    // Entity → Domain
    private fun ColorEntity.toDomain(): FeatureColor =
        FeatureColor(
            id = id,
            name = name,
            lightRed = lightRed,
            lightGreen = lightGreen,
            lightBlue = lightBlue,
            lightAlpha = lightAlpha,
            darkRed = darkRed,
            darkGreen = darkGreen,
            darkBlue = darkBlue,
            darkAlpha = darkAlpha,
            lightTextRed = lightTextRed,
            lightTextGreen = lightTextGreen,
            lightTextBlue = lightTextBlue,
            lightTextAlpha = lightTextAlpha,
            darkTextRed = darkTextRed,
            darkTextGreen = darkTextGreen,
            darkTextBlue = darkTextBlue,
            darkTextAlpha = darkTextAlpha
        )

    // Domain → Entity
    private fun FeatureColor.toEntity(): ColorEntity =
        ColorEntity(
            id = id,
            name = name,
            lightRed = lightRed,
            lightGreen = lightGreen,
            lightBlue = lightBlue,
            lightAlpha = lightAlpha,
            darkRed = darkRed,
            darkGreen = darkGreen,
            darkBlue = darkBlue,
            darkAlpha = darkAlpha,
            lightTextRed = lightTextRed,
            lightTextGreen = lightTextGreen,
            lightTextBlue = lightTextBlue,
            lightTextAlpha = lightTextAlpha,
            darkTextRed = darkTextRed,
            darkTextGreen = darkTextGreen,
            darkTextBlue = darkTextBlue,
            darkTextAlpha = darkTextAlpha,
        )

    // Create → Entity
    private fun CreateFeatureColor.toEntity(): ColorEntity =
        ColorEntity(
            id = 0,
            name = name,
            lightRed = lightRed,
            lightGreen = lightGreen,
            lightBlue = lightBlue,
            lightAlpha = lightAlpha,
            darkRed = darkRed,
            darkGreen = darkGreen,
            darkBlue = darkBlue,
            darkAlpha = darkAlpha,
            lightTextRed = lightTextRed,
            lightTextGreen = lightTextGreen,
            lightTextBlue = lightTextBlue,
            lightTextAlpha = lightTextAlpha,
            darkTextRed = darkTextRed,
            darkTextGreen = darkTextGreen,
            darkTextBlue = darkTextBlue,
            darkTextAlpha = darkTextAlpha,
            isDefault = isDefault
        )
}

