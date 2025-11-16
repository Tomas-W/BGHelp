package com.example.bghelp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bghelp.constants.DatabaseConstants as DB

@Entity(tableName = DB.COLOR_TABLE)
data class ColorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Float,
    val isDefault: Boolean = false
) {
    companion object {
        const val COL_ID = "id"
    }
}

