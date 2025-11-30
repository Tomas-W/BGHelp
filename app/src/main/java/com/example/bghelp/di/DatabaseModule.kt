package com.example.bghelp.di

import android.content.Context
import androidx.room.Room
import com.example.bghelp.constants.DatabaseConstants as DB
import com.example.bghelp.data.local.AppDatabase
import com.example.bghelp.data.local.dao.ColorDao
import com.example.bghelp.data.local.dao.ItemDao
import com.example.bghelp.data.local.dao.TargetDao
import com.example.bghelp.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB.DB_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideTargetDao(database: AppDatabase): TargetDao {
        return database.targetDao()
    }

    @Provides
    @Singleton
    fun provideItemDao(database: AppDatabase): ItemDao {
        return database.itemDao()
    }

    @Provides
    @Singleton
    fun provideColorDao(database: AppDatabase): ColorDao {
        return database.colorDao()
    }
}
