package com.example.bghelp.data.repository

import com.example.bghelp.data.local.dao.ItemDao
import com.example.bghelp.data.local.entity.ItemEntity
import com.example.bghelp.domain.model.CreateItem
import com.example.bghelp.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ItemRepository {
    suspend fun addItem(createItem: CreateItem)
    suspend fun updateItem(item: Item)
    suspend fun deleteItem(item: Item)
    fun getItemById(id: Int): Flow<Item?>
    fun getAllItems(): Flow<List<Item>>
    fun getAllGroups(): Flow<List<String>>
    suspend fun deleteGroup(itemGroup: String)
}

class ItemRepositoryImpl(private val itemDao: ItemDao) : ItemRepository {
    override suspend fun addItem(createItem: CreateItem) {
        val entity = createItem.toEntity()
        itemDao.addItem(entity)
    }

    override suspend fun updateItem(item: Item) {
        itemDao.updateItem(item.toEntity())
    }

    override suspend fun deleteItem(item: Item) {
        itemDao.deleteItem(item.toEntity())
    }

    override fun getItemById(id: Int): Flow<Item?> =
        itemDao.getItemById(id).map { it?.toDomain() }

    override fun getAllItems(): Flow<List<Item>> =
        itemDao.getAllItems().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getAllGroups(): Flow<List<String>> =
        itemDao.getAllGroups()
    
    override suspend fun deleteGroup(itemGroup: String) {
        itemDao.deleteGroup(itemGroup)
    }

    // Entity → Domain
    private fun ItemEntity.toDomain(): Item {
        return Item(
            id = id,
            itemGroup = itemGroup,
            name = name,
            quantity = quantity,
            unit = unit,
            bought = bought
        )
    }

    // Domain → Entity
    private fun Item.toEntity(): ItemEntity {
        return ItemEntity(
            id = id,
            itemGroup = itemGroup,
            name = name,
            quantity = quantity,
            unit = unit,
            bought = bought
        )
    }

    // CreateItem → Entity
    private fun CreateItem.toEntity(): ItemEntity {
        return ItemEntity(
            id = 0, // db will auto-generate
            itemGroup = itemGroup,
            name = name,
            quantity = quantity,
            unit = unit,
            bought = bought
        )
    }
}
