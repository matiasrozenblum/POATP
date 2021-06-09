package com.mrozenblum.poatp

import com.mrozenblum.poatp.domain.Item
import com.mrozenblum.poatp.domain.TransactionBody
import com.mrozenblum.poatp.domain.TransactionResponse
import com.mrozenblum.poatp.domain.User
import com.mrozenblum.poatp.storage.ItemStorage
import com.mrozenblum.poatp.storage.TransactionItemStorage
import com.mrozenblum.poatp.storage.TransactionStatus.FAILED
import com.mrozenblum.poatp.storage.TransactionStatus.SUCCESS
import com.mrozenblum.poatp.storage.TransactionStorage
import com.mrozenblum.poatp.storage.UserStorage
import org.springframework.stereotype.Service

@Service
class Service(
    private val transactionStorage: TransactionStorage,
    private val userStorage: UserStorage,
    private val itemStorage: ItemStorage,
    private val transactionItemStorage: TransactionItemStorage
) {
    fun saveUser(user: User) = userStorage.store(user)

    fun getUser(userId: Long) = userStorage.search(userId)

    fun getUserByEmail(email: String) = userStorage.searchByEmail(email)

    fun deleteUser(userId: Long) = userStorage.delete(userId)

    fun deleteUserByEmail(email: String) = userStorage.deleteByEmail(email)

    fun saveItem(item: Item) = itemStorage.store(item)

    fun getItem(itemId: Long) = itemStorage.search(itemId)

    fun deleteItem(itemId: Long) = itemStorage.delete(itemId)

    fun createTransaction(transactionBody: TransactionBody): TransactionResponse {
        var value: Long = 0
        transactionBody.items.forEach {
            val item = getItem(it)
            value += item.value
        }
        return transactionStorage.store(transactionBody.copy(value = value))
    }

    fun getTransaction(transactionId: Long) = transactionStorage.search(transactionId)

    fun deleteTransaction(transactionId: Long) = transactionStorage.delete(transactionId)

    fun closeTransaction(transactionId: Long): Boolean {
        val transaction = getTransaction(transactionId)
        val userPoints = userStorage.getPoints(transaction.userId)
        var status = FAILED
        if (userPoints >= transaction.value!!) {
            status = SUCCESS
            userStorage.discountPoints(transaction.userId, userPoints - transaction.value)
        }
        val result = transactionStorage.updateStatus(transactionId, status)
        if (status == FAILED) {
            throw NotEnoughPointsException()
        }
        return result
    }
}