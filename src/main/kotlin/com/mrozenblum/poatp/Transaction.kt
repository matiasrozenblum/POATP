package com.mrozenblum.poatp

data class Transaction(
    val id: Long? = null,
    val userId: Long,
    val items: List<Long>,
    val value: Long? = null,
    val status: String? = null
)

data class TransactionResponse(
    val transactionId: Long
)

fun Transaction.asResponse() = TransactionResponse(id!!)