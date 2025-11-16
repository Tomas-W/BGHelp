package com.example.bghelp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bghelp.constants.DatabaseConstants as DB

@Entity(tableName = DB.ITEM_TABLE)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val item_group: String,
    val name: String,
    val quantity: Float?,
    val unit: String?,
    val bought: Boolean
)
