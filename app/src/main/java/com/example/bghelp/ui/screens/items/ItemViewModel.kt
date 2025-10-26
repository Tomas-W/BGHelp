package com.example.bghelp.ui.screens.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.repository.ItemRepository
import com.example.bghelp.domain.model.CreateItem
import com.example.bghelp.domain.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {
    val allItems: StateFlow<List<Item>> = itemRepository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val itemsGrouped: StateFlow<Map<String, List<Item>>> = allItems
        .map { items ->
            items.groupBy { it.item_group }
                .mapValues { (_, itemsInGroup) ->
                    itemsInGroup.sortedBy { it.name }
                }
                .toSortedMap() // Sort groups alphabetically
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun addItem(createItem: CreateItem) {
        viewModelScope.launch {
            itemRepository.addItem(createItem)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            itemRepository.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemRepository.deleteItem(item)
        }
    }

    fun markItemAsBought(item: Item) {
        val updatedItem = item.copy(bought = !item.bought)
        updateItem(updatedItem)
    }

    fun deleteGroup(itemGroup: String) {
        viewModelScope.launch {
            itemRepository.deleteGroup(itemGroup)
        }
    }

    fun getItemById(id: Int): Flow<Item?> {
        return itemRepository.getItemById(id)
    }
}
