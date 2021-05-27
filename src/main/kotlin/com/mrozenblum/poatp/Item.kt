package com.mrozenblum.poatp

data class Item(
    val id: Long? = null,
    val name: String,
    val value: Long
)

data class ItemResponse(
    val transactionId: Long
)

fun Item.asResponse() = ItemResponse(id!!)