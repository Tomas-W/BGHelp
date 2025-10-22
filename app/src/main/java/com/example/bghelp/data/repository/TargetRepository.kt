package com.example.bghelp.data.repository

import com.example.bghelp.domain.model.Target
import com.example.bghelp.domain.model.CreateTarget
import com.example.bghelp.data.local.TargetDao
import com.example.bghelp.data.local.TargetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId

interface TargetRepository {
    suspend fun addTarget(createTarget: CreateTarget)
    suspend fun updateTarget(target: Target)
    suspend fun deleteTarget(target: Target)
    fun getTargetById(id: Int): Flow<Target?>
    fun getAllTargets(): Flow<List<Target>>
    fun getTargetByDateRange(startDate: Long, endDate: Long): Flow<List<Target>>
    fun getNextTarget(currentTime: Long): Flow<Target?>
}

class TargetRepositoryImpl(private val targetDao: TargetDao) : TargetRepository {

    override suspend fun addTarget(createTarget: CreateTarget) {
        val entity = createTarget.toEntity()
        targetDao.addTarget(entity)
    }

    override suspend fun updateTarget(target: Target) {
        targetDao.updateTarget(target.toEntity())
    }

    override suspend fun deleteTarget(target: Target) {
        targetDao.deleteTarget(target.toEntity())
    }

    override fun getTargetById(id: Int): Flow<Target?> = 
        targetDao.getTargetById(id).map { it?.toDomain() }

    override fun getAllTargets(): Flow<List<Target>> = 
        targetDao.getAllTargets().map { entities -> 
            entities.map { it.toDomain() } 
        }

    override fun getTargetByDateRange(startDate: Long, endDate: Long): Flow<List<Target>> = 
        targetDao.getTargetByDateRange(startDate, endDate).map { entities -> 
            entities.map { it.toDomain() } 
        }

    override fun getNextTarget(currentTime: Long): Flow<Target?> = 
        targetDao.getNextTarget(currentTime).map { it?.toDomain() }
    
    // Private mapping functions
    
    // Entity → Domain
    private fun TargetEntity.toDomain(): Target {
        val localDateTime = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        
        return Target(
            id = id,
            dateTime = localDateTime,
            message = message,
            expired = expired,
            coordinates = coordinates,
            alertDistance = alertDistance,
            alarmName = alarmName,
            sound = sound,
            vibrate = vibrate,
            snoozeTime = snoozeTime
        )
    }
    
    // Domain → Entity
    private fun Target.toEntity(): TargetEntity {
        val epochMillis = dateTime.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        
        return TargetEntity(
            id = id,
            date = epochMillis,
            message = message,
            expired = expired,
            coordinates = coordinates,
            alertDistance = alertDistance,
            alarmName = alarmName,
            sound = sound,
            vibrate = vibrate,
            snoozeTime = snoozeTime
        )
    }
    
    // CreateTarget → Entity
    private fun CreateTarget.toEntity(): TargetEntity {
        val epochMillis = date.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        
        return TargetEntity(
            id = 0,  // Auto-generated
            date = epochMillis,
            message = message,
            expired = expired,
            coordinates = coordinates,
            alertDistance = alertDistance,
            alarmName = alarmName,
            sound = sound,
            vibrate = vibrate,
            snoozeTime = snoozeTime
        )
    }
}