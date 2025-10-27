package com.example.bghelp.ui.screens.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.repository.ItemRepository
import com.example.bghelp.domain.model.CreateItem
import com.example.bghelp.domain.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ItemListItem {
    data class Header(val title: String) : ItemListItem()
    data class ItemData(val item: Item) : ItemListItem()
    data class Spacer(val id: String) : ItemListItem()
}

@HiltViewModel
class ItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {
    private val _savedScrollIndex = MutableStateFlow(0)
    private val _savedScrollOffset = MutableStateFlow(0)

    fun saveScrollPosition(index: Int, offset: Int) {
        _savedScrollIndex.value = index
        _savedScrollOffset.value = offset
    }

    fun getSavedScrollIndex(): Int = _savedScrollIndex.value
    fun getSavedScrollOffset(): Int = _savedScrollOffset.value
    
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
                .toSortedMap()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val flattenedItems: StateFlow<List<ItemListItem>> = itemsGrouped
        .map { grouped ->
            buildList {
                grouped.forEach { (groupName, items) ->
                    add(ItemListItem.Header(groupName))
                    items.forEach { item ->
                        add(ItemListItem.ItemData(item))
                    }
                    add(ItemListItem.Spacer("spacer_$groupName"))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
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
