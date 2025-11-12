package com.example.bghelp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        TaskEntity::class,
        TaskReminderEntity::class,
        TaskLocationEntity::class,
        TargetEntity::class,
        ItemEntity::class
    ],
    version = 5
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun targetDao(): TargetDao
    abstract fun itemDao(): ItemDao
}
