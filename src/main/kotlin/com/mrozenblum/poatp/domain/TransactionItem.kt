package com.mrozenblum.poatp.domain

data class TransactionItem(
    val id: Long? = null,
    val transactionId: Long,
    val itemId: Long
)