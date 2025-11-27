package com.example.bghelp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bghelp.data.local.dao.ColorDao
import com.example.bghelp.data.local.dao.ItemDao
import com.example.bghelp.data.local.dao.TargetDao
import com.example.bghelp.data.local.dao.TaskDao
import com.example.bghelp.data.local.entity.ColorEntity
import com.example.bghelp.data.local.entity.ItemEntity
import com.example.bghelp.data.local.entity.TargetEntity
import com.example.bghelp.data.local.entity.TaskEntity
import com.example.bghelp.data.local.entity.TaskLocationEntity
import com.example.bghelp.data.local.entity.TaskReminderEntity

@Database(
    entities = [
        TaskEntity::class,
        TaskReminderEntity::class,
        TaskLocationEntity::class,
        TargetEntity::class,
        ItemEntity::class,
        ColorEntity::class
    ],
    version = 23
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun targetDao(): TargetDao
    abstract fun itemDao(): ItemDao
    abstract fun colorDao(): ColorDao
}
