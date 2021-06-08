package com.mrozenblum.poatp.domain

data class Transaction(
    val id: Long? = null,
    val userId: Long,
    val items: List<Item>,
    val value: Long? = null,
    val status: String? = null
)

data class TransactionBody(
    val id: Long? = null,
    val userId: Long,
    val items: List<Long>,
    val value: Long? = null,
    val status: String? = null
)

data class TransactionResponse(
    val transactionId: Long
)

fun TransactionBody.toTransaction(items: List<Item>) = Transaction(
    id,
    userId,
    items,
    value,
    status
)