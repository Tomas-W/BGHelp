package com.example.bghelp.data.repository

import com.example.bghelp.data.local.TargetDao
import com.example.bghelp.data.local.TargetEntity
import kotlinx.coroutines.flow.Flow


class TargetRepository(private val targetDao: TargetDao) {

    suspend fun addTarget(target: TargetEntity) = targetDao.addTarget(target)

    suspend fun updateTarget(target: TargetEntity) = targetDao.updateTarget(target)

    suspend fun deleteTarget(target: TargetEntity) = targetDao.deleteTarget(target)

    fun getTargetById(id: Int): Flow<TargetEntity?> = targetDao.getTargetById(id)

    fun getAllTargets(): Flow<List<TargetEntity>> = targetDao.getAllTargets()

    fun getTargetByDateRange(startDate: Long, endDate: Long): Flow<List<TargetEntity>> = targetDao.getTargetByDateRange(startDate, endDate)

    fun getNextTarget(currentTime: Long): Flow<TargetEntity?> = targetDao.getNextTarget(currentTime)
}