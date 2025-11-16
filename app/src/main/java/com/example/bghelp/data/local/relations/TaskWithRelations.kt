package com.example.bghelp.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.bghelp.data.local.entity.ColorEntity
import com.example.bghelp.data.local.entity.TaskEntity
import com.example.bghelp.data.local.entity.TaskLocationEntity
import com.example.bghelp.data.local.entity.TaskReminderEntity

data class TaskWithRelations(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = TaskEntity.COL_COLOR_ID,
        entityColumn = ColorEntity.COL_ID
    )
    val color: ColorEntity?,
    @Relation(
        parentColumn = TaskEntity.COL_ID,
        entityColumn = TaskReminderEntity.COL_TASK_ID
    )
    val reminders: List<TaskReminderEntity>,
    @Relation(
        parentColumn = TaskEntity.COL_ID,
        entityColumn = TaskLocationEntity.COL_TASK_ID
    )
    val locations: List<TaskLocationEntity>
)


