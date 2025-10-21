package com.example.bghelp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
abstract class TargetDao {

    @Insert
    abstract suspend fun addTarget(target: TargetEntity)

    @Update
    abstract suspend fun updateTarget(target: TargetEntity)

    @Delete
    abstract suspend fun deleteTarget(target: TargetEntity)

    @Query("""
        SELECT * FROM target_table
        WHERE id=:id
        """)
    abstract fun getTargetById(id: Int): Flow<TargetEntity?>

    @Query("""
        SELECT * FROM target_table
        ORDER BY date
        ASC
        """)
    abstract fun getAllTargets(): Flow<List<TargetEntity>>

    @Query("""
        SELECT * FROM target_table
        WHERE date BETWEEN :startDate
        AND :endDate
        ORDER BY date
        ASC
        """)
    abstract fun getTargetByDateRange(startDate: Long, endDate: Long): Flow<List<TargetEntity>>

    @Query("""
        SELECT * FROM target_table
        WHERE date > :currentTime
        ORDER BY date
        ASC
        LIMIT 1
        """)
    abstract fun getNextTarget(currentTime: Long): Flow<TargetEntity?>

}
