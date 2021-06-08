package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.ItemNotFoundException
import com.mrozenblum.poatp.domain.TransactionItem
import com.mrozenblum.poatp.storage.TransactionItemTable.transactionId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.sql.Connection

object TransactionItemTable : Table("transaction_item") {
    val id: Column<Long> = long("id").autoIncrement()
    val transactionId: Column<Long> = long("transaction_id")
    val itemId: Column<Long> = long("item_id")

    override val primaryKey = PrimaryKey(id)
}

@Service
class TransactionItemStorage {

    fun store(transactionItem: TransactionItem) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            TransactionItemTable.insert {
                it[transactionId] = transactionItem.transactionId
                it[itemId] = transactionItem.itemId
            }
        }
    }

    fun search(id: Long): TransactionItem {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                TransactionItemTable
                    .select { TransactionItemTable.id eq it }
                    .map { it.toTransactionItem() }.firstOrNull() ?: throw ItemNotFoundException()
            }
        }
    }

    fun searchByTransactionId(transactionid: Long): List<TransactionItem> {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                TransactionItemTable
                    .select { transactionId eq transactionid }
                    .map { it.toTransactionItem() }
            }
        }
    }

    fun delete(id: Long): Boolean {
        val result = transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                TransactionItemTable
                    .deleteWhere { TransactionItemTable.id eq it }
            }
        }
        return result == 1
    }
}

fun ResultRow.toTransactionItem() = TransactionItem(
    id = this[TransactionItemTable.id],
    transactionId = this[transactionId],
    itemId = this[TransactionItemTable.itemId]
)