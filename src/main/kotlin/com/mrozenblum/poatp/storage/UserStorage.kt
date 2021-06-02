package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.User
import com.mrozenblum.poatp.UserNotFoundException
import com.mrozenblum.poatp.UserResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.sql.Connection

object UserTable : Table("user") {
    val id: Column<Long> = long("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val email: Column<String> = varchar("email", 150)
    val points: Column<Long> = long("points")

    override val primaryKey = PrimaryKey(id)
}

@Service
class UserStorage {

    fun store(user: User): UserResponse {
        return UserResponse(transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            UserTable.insert {
                it[name] = user.name
                it[email] = user.email
                it[points] = user.points
            } get UserTable.id
        })
    }

    fun search(id: Long): User {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                UserTable
                    .select { UserTable.id eq it }
                    .map { it.toUser() }.firstOrNull() ?: throw UserNotFoundException()
            }
        }
    }

    fun searchByEmail(email: String): User {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            email.let {
                UserTable
                    .select { UserTable.email eq it }
                    .map { it.toUser() }.firstOrNull() ?: throw UserNotFoundException()
            }
        }
    }

    fun delete(id: Long): Boolean {
        val result = transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                UserTable
                    .deleteWhere { UserTable.id eq it }
            }
        }
        return result == 1
    }

    fun deleteByEmail(email: String): Boolean {
        val result = transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            email.let {
                UserTable
                    .deleteWhere { UserTable.email eq it }
            }
        }
        return result == 1
    }

    fun getPoints(id: Long): Long {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                UserTable.slice(UserTable.points)
                    .select { UserTable.id eq it }.limit(1)
                    .map { it[UserTable.points] }.firstOrNull() ?: 0
            }
        }
    }

    fun discountPoints(id: Long, value: Long): Boolean {
        val result = transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 1) {
            id.let {
                UserTable
                    .update({ UserTable.id eq it }) { it[points] = value }
            }
        }
        return result == 1
    }
}

fun ResultRow.toUser() = User(
    id = this[UserTable.id],
    name = this[UserTable.name],
    email = this[UserTable.email],
    points = this[UserTable.points]
)