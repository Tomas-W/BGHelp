package com.example.bghelp.ui.screens.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bghelp.domain.model.CreateItem
import com.example.bghelp.ui.components.HighlightedContainerSmall
import com.example.bghelp.ui.components.LazyColumnContainer
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.MainHeader
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun ItemScreen(
    viewModel: ItemViewModel,
    modifier: Modifier = Modifier
) {
    val flattenedItems by viewModel.flattenedItems.collectAsState()
    
    val listState = rememberLazyListState()
    val savedIndex = viewModel.getSavedScrollIndex()
    val savedOffset = viewModel.getSavedScrollOffset()
    
    LaunchedEffect(flattenedItems) {
        if (flattenedItems.isNotEmpty() && savedIndex > 0) {
            listState.scrollToItem(savedIndex, savedOffset)
        }
    }
    
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        viewModel.saveScrollPosition(
            listState.firstVisibleItemIndex,
            listState.firstVisibleItemScrollOffset
        )
    }

    MainContentContainer {
        LazyColumnContainer(state = listState) {
            item {
                AddItemButton(viewModel)
            }

            items(
                items = flattenedItems,
                key = { listItem ->
                    when (listItem) {
                        is ItemListItem.Header -> "header_${listItem.title}"
                        is ItemListItem.ItemData -> "item_${listItem.item.id}"
                        is ItemListItem.Spacer -> listItem.id
                    }
                }
            ) { listItem ->
                when (listItem) {
                    is ItemListItem.Header -> {
                        MainHeader(listItem.title)
                    }
                    is ItemListItem.ItemData -> {
                        val item = listItem.item
                        val color = if (!item.bought) MaterialTheme.colorScheme.secondary
                                                 else MaterialTheme.colorScheme.tertiary
                        HighlightedContainerSmall(
                            backgroundColor = color
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    item.name,
                                    style = TextStyles.Default.Medium,
                                    modifier = Modifier.weight(2f)
                                )
                                Text(
                                    item.quantity.toString(),
                                    style = TextStyles.Default.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    item.unit.toString(),
                                    style = TextStyles.Default.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    tint = Color.Red,
                                    contentDescription = "Delete",
                                    modifier = Modifier.clickable { viewModel.deleteItem(item) }
                                )
                            }
                        }
                    }
                    is ItemListItem.Spacer -> {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
    }

    }
}


@Composable
fun AddItemButton(viewModel: ItemViewModel) {
    Button(
        onClick = { 
            val randomGroup = listOf("Food", "Drink", "Household", "Personal", "Other").random()
            val randomName = listOf("Apple", "Banana", "Orange", "Milk", "Bread", "Cheese", "Butter").random()
            val randomQuantity = (1..10).random().toFloat()
            val randomUnit = listOf("kg", "g", "l", "ml", "pcs").random()
            val randomBought = (0..1).random() == 1
            
            viewModel.addItem(
                CreateItem(
                    randomGroup,
                    randomName,
                    randomQuantity,
                    randomUnit,
                    randomBought
                )
            )
        }
    ) {
        Text("Add item")
    }
}
