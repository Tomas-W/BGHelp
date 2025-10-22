package com.example.bghelp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bghelp.utils.AlarmMode


@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val message: String,
    val expired: Boolean,
    val alarmName: String?,
    val sound: AlarmMode,
    val vibrate: AlarmMode,
    val snoozeTime: Int
)