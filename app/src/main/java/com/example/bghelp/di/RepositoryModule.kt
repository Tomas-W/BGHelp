package com.example.bghelp.di

import com.example.bghelp.data.local.dao.ColorDao
import com.example.bghelp.data.local.dao.ItemDao
import com.example.bghelp.data.local.dao.TargetDao
import com.example.bghelp.data.local.dao.TaskDao
import com.example.bghelp.data.repository.ColorRepository
import com.example.bghelp.data.repository.ColorRepositoryImpl
import com.example.bghelp.data.repository.ItemRepository
import com.example.bghelp.data.repository.ItemRepositoryImpl
import com.example.bghelp.data.repository.TargetRepository
import com.example.bghelp.data.repository.TargetRepositoryImpl
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.data.repository.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }

    @Provides
    @Singleton
    fun provideTargetRepository(targetDao: TargetDao): TargetRepository {
        return TargetRepositoryImpl(targetDao)
    }

    @Provides
    @Singleton
    fun provideItemRepository(itemDao: ItemDao): ItemRepository {
        return ItemRepositoryImpl(itemDao)
    }

    @Provides
    @Singleton
    fun provideColorRepository(colorDao: ColorDao): ColorRepository {
        return ColorRepositoryImpl(colorDao)
    }
}
