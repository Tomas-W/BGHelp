package com.example.bghelp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bghelp.constants.DatabaseConstants as DB
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.TaskColorOption
import com.example.bghelp.domain.model.TaskImageSourceOption

@Entity(tableName = DB.TASK_TABLE)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val info: String?,
    val note: String?,
    val startEpoch: Long,
    val endEpoch: Long?,
    val allDay: Boolean,
    val rrule: String?,
    val expired: Boolean,
    val alarmName: String?,
    val sound: AlarmMode,
    val vibrate: AlarmMode,
    val soundUri: String?,
    val snoozeMinutes: Int,
    val color: TaskColorOption,
    val imageUri: String?,
    val imageName: String?,
    val imageSource: TaskImageSourceOption?,
    val createdAt: Long,
    val updatedAt: Long
)
