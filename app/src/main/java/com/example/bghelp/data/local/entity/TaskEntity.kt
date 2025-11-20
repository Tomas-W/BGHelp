package com.example.bghelp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bghelp.constants.DatabaseConstants as DB
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.ReminderOffsetUnit
import com.example.bghelp.domain.model.TaskImageSourceOption

@Entity(
    tableName = DB.TASK_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = ColorEntity::class,
            parentColumns = [ColorEntity.COL_ID],
            childColumns = [TaskEntity.COL_COLOR_ID],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = [TaskEntity.COL_COLOR_ID])
    ]
)
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
    val snoozeValue1: Int,
    val snoozeUnit1: ReminderOffsetUnit,
    val snoozeValue2: Int,
    val snoozeUnit2: ReminderOffsetUnit,
    val colorId: Int = 1,
    val imageUri: String?,
    val imageName: String?,
    val imageSource: TaskImageSourceOption?,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        const val COL_ID = "id"
        const val COL_COLOR_ID = "colorId"
    }
}
