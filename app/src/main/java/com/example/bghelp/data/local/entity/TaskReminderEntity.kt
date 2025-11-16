package com.example.bghelp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bghelp.constants.DatabaseConstants as DB
import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.ReminderOffsetUnit

@Entity(
    tableName = DB.TASK_REMINDER_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = [TaskEntity.COL_ID],
            childColumns = [TaskReminderEntity.COL_TASK_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [TaskReminderEntity.COL_TASK_ID])
    ]
)
data class TaskReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val type: ReminderKind,
    val offsetValue: Int,
    val offsetUnit: ReminderOffsetUnit
) {
    companion object {
        const val COL_TASK_ID = "taskId"
    }
}

