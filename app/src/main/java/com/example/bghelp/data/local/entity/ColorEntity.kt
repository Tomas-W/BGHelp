package com.example.bghelp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bghelp.constants.DatabaseConstants as DB

@Entity(tableName = DB.COLOR_TABLE)
data class ColorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    // Task color Light
    val lightRed: Int,
    val lightGreen: Int,
    val lightBlue: Int,
    val lightAlpha: Float,
    // Task color Dark
    val darkRed: Int,
    val darkGreen: Int,
    val darkBlue: Int,
    val darkAlpha: Float,
    // Text color light
    val lightTextRed: Int = 0,
    val lightTextGreen: Int = 0,
    val lightTextBlue: Int = 0,
    val lightTextAlpha: Float = 1.0f,
    // Text color dark
    val darkTextRed: Int = 0,
    val darkTextGreen: Int = 0,
    val darkTextBlue: Int = 0,
    val darkTextAlpha: Float = 1.0f,
    val isDefault: Boolean = false
) {
    companion object {
        const val COL_ID = "id"
    }
}

