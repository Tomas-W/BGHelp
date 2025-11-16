package com.example.bghelp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bghelp.data.local.entity.ColorEntity
import com.example.bghelp.constants.DatabaseConstants as DB
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ColorDao {
    @Query("SELECT * FROM ${DB.COLOR_TABLE}")
    abstract fun getAllColors(): Flow<List<ColorEntity>>
    
    @Query("SELECT * FROM ${DB.COLOR_TABLE} WHERE id = :id")
    abstract suspend fun getColorById(id: Int): ColorEntity?
    
    @Query("SELECT * FROM ${DB.COLOR_TABLE} WHERE isDefault = 1")
    abstract fun getDefaultColors(): Flow<List<ColorEntity>>
    
    @Insert
    abstract suspend fun insertColor(color: ColorEntity): Long
    
    @Update
    abstract suspend fun updateColor(color: ColorEntity)
    
    @Delete
    abstract suspend fun deleteColor(color: ColorEntity)
}

