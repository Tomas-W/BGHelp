package com.example.bghelp.domain.model

data class CreateItem(
    val itemGroup: String,
    val name: String,
    val quantity: Float?,
    val unit: String?,
    val bought: Boolean
)

data class Item(
    val id: Int,
    val itemGroup: String,
    val name: String,
    val quantity: Float?,
    val unit: String?,
    val bought: Boolean
)
