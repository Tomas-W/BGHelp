package com.example.bghelp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bghelp.constants.DatabaseConstants as DB
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.Coordinate

@Entity(tableName = DB.TARGET_TABLE)
data class TargetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val message: String,
    val expired: Boolean,
    val coordinates: List<Coordinate>,
    val alertDistance: Int,
    val alarmName: String?,
    val sound: AlarmMode,
    val vibrate: AlarmMode,
    val snoozeTime: Int
)
