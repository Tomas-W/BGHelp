package com.example.bghelp.domain.model

data class CreateItem(
    val item_group: String,
    val name: String,
    val quantity: Float?,
    val unit: String?,
    val bought: Boolean
)

data class Item(
    val id: Int,
    val item_group: String,
    val name: String,
    val quantity: Float?,
    val unit: String?,
    val bought: Boolean
)
