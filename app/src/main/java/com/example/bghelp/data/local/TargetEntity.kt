package com.example.bghelp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.utils.Coordinate


@Entity(tableName = "target_table")
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
