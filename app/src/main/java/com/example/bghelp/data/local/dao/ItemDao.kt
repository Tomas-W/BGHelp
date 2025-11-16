package com.example.bghelp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bghelp.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow
import com.example.bghelp.constants.DatabaseConstants as DB

@Dao
abstract class ItemDao {
    @Insert
    abstract suspend fun addItem(item: ItemEntity)

    @Update
    abstract suspend fun updateItem(item: ItemEntity)

    @Delete
    abstract suspend fun deleteItem(item: ItemEntity)

    @Query("""
        SELECT * FROM ${DB.ITEM_TABLE}
        WHERE id = :id
    """)
    abstract fun getItemById(id: Int): Flow<ItemEntity?>

    @Query("""
        SELECT * FROM ${DB.ITEM_TABLE}
        ORDER BY item_group ASC, name ASC
    """)
    abstract fun getAllItems(): Flow<List<ItemEntity>>

    @Query("""
        SELECT DISTINCT item_group FROM ${DB.ITEM_TABLE}
        ORDER BY item_group ASC
    """)
    abstract fun getAllGroups(): Flow<List<String>>

    @Query("""
        DELETE FROM ${DB.ITEM_TABLE}
        WHERE item_group = :itemGroup
    """)
    abstract suspend fun deleteGroup(itemGroup: String)

}
