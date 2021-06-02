package com.mrozenblum.poatp.domain

data class Item(
    val id: Long? = null,
    val name: String,
    val value: Long
)

data class ItemResponse(
    val itemId: Long
)