package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.TransactionNotFoundException
import com.mrozenblum.poatp.domain.*
import com.mrozenblum.poatp.domain.Transaction
import com.mrozenblum.poatp.storage.TransactionStatus.OPEN
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.sql.Connection

object TransactionTable : Table("transaction") {
    val id: Column<Long> = long("id").autoIncrement()
    val userId: Column<Long> = long("user_id")
    val value: Column<Long> = long("value")
    val status: Column<String> = varchar("status", 50).default(OPEN.name)

    override val primaryKey = PrimaryKey(id)
}

@Service
class TransactionStorage(
    val itemStorage: ItemStorage,
    val transactionItemStorage: TransactionItemStorage
) {

    fun store(transactionBody: TransactionBody): TransactionResponse {
        val transactionId = transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            TransactionTable.insert {
                it[userId] = transactionBody.userId
                it[value] = transactionBody.value!!
            } get TransactionTable.id
        }
        transactionBody.items.forEach {
            val transactionItem = TransactionItem(transactionId = transactionId, itemId = it)
            transactionItemStorage.store(transactionItem)
        }
        return TransactionResponse(transactionId)
    }

    fun search(id: Long): Transaction {
        val itemIds = transactionItemStorage.searchByTransactionId(id)
        val items = itemStorage.searchByIdList(itemIds)
        return transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                TransactionTable
                    .select { TransactionTable.id eq it }.limit(1)
                    .map { it.toTransaction(items) }.firstOrNull() ?: throw TransactionNotFoundException()
            }
        }
    }

    fun delete(id: Long): Boolean {
        transactionItemStorage.deleteByTransactionId(id)
        val result = transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                TransactionTable
                    .deleteWhere { TransactionTable.id eq it }
            }
        }
        return result == 1
    }

    fun updateStatus(id: Long, newStatus: TransactionStatus): Boolean {
        val result = transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                TransactionTable
                    .update({ TransactionTable.id eq it }) { it[status] = newStatus.name }
            }
        }
        return result == 1
    }
}

enum class TransactionStatus { OPEN, SUCCESS, FAILED }

fun ResultRow.toTransaction(items: List<Item>) = Transaction(
    id = this[TransactionTable.id],
    userId = this[TransactionTable.userId],
    items = items,
    value = this[TransactionTable.value],
    status = this[TransactionTable.status]
)