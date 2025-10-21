package com.example.bghelp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.utils.Coordinate
import com.example.bghelp.utils.SchedulableItem


@Entity(tableName = "target_table")
data class TargetEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val date: Long,
    override val message: String,
    val expired: Boolean,
    val coordinates: List<Coordinate>,
    val alertDistance: Int,
    val alarmName: String?,
    override val sound: AlarmMode,
    override val vibrate: AlarmMode,
    override val snoozeTime: Int
): SchedulableItem
