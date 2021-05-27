package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.Transaction
import com.mrozenblum.poatp.TransactionResponse
import com.mrozenblum.poatp.storage.TransactionStatus.OPEN
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.sql.Connection

object TransactionTable : Table("transaction") {
    val id: Column<Long> = long("id").autoIncrement()
    val userId: Column<Long> = long("user_id")
    val items: Column<String> = varchar("items", 1000)
    val value: Column<Long> = long("value")
    val status: Column<String> = varchar("status", 50).default(OPEN.name)

    override val primaryKey = PrimaryKey(id)
}

@Service
class TransactionStorage {

    fun store(transaction: Transaction): TransactionResponse {
        return TransactionResponse(transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            TransactionTable.insert {
                it[userId] = transaction.userId
                it[items] = transaction.items.joinToString()
                it[value] = transaction.value!!
            } get TransactionTable.id
        })
    }

    fun search(id: Long): Transaction {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                TransactionTable
                    .select { TransactionTable.id eq it }.limit(1)
                    .map { it.toTransaction() }.firstOrNull() ?: throw RuntimeException()
            }
        }
    }

    fun delete(id: Long): Boolean {
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

fun ResultRow.toTransaction() = Transaction(
    id = this[TransactionTable.id],
    userId = this[TransactionTable.userId],
    items = this[TransactionTable.items].removeSurrounding("[", "]").split(", ").map { it.toLong() },
    value = this[TransactionTable.value],
    status = this[TransactionTable.status]
)