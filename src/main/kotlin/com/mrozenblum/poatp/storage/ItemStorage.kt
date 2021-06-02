package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.ItemNotFoundException
import com.mrozenblum.poatp.domain.Item
import com.mrozenblum.poatp.domain.ItemResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.sql.Connection

object ItemTable : Table("item") {
    val id: Column<Long> = long("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val value: Column<Long> = long("value")

    override val primaryKey = PrimaryKey(id)
}

@Service
class ItemStorage {

    fun store(item: Item): ItemResponse {
        return ItemResponse(transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            ItemTable.insert {
                it[name] = item.name
                it[value] = item.value
            } get ItemTable.id
        })
    }

    fun search(id: Long): Item {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                ItemTable
                    .select { ItemTable.id eq it }
                    .map { it.toItem() }.firstOrNull() ?: throw ItemNotFoundException()
            }
        }
    }

    fun delete(id: Long): Boolean {
        val result = transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                ItemTable
                    .deleteWhere { ItemTable.id eq it }
            }
        }
        return result == 1
    }
}

fun ResultRow.toItem() = Item(
    id = this[ItemTable.id],
    name = this[ItemTable.name],
    value = this[ItemTable.value]
)