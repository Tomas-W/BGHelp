package com.example.bghelp.di

import com.example.bghelp.data.local.TargetDao
import com.example.bghelp.data.local.TaskDao
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
}