package com.example.bghelp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bghelp.constants.DatabaseConstants as DB

@Entity(
    tableName = DB.TASK_LOCATION_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = [TaskEntity.COL_ID],
            childColumns = [TaskLocationEntity.COL_TASK_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [TaskLocationEntity.COL_TASK_ID])
    ]
)
data class TaskLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val name: String,
    val orderIndex: Int
) {
    companion object {
        const val COL_TASK_ID = "taskId"
    }
}

