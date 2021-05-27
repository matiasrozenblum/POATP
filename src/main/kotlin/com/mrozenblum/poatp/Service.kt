package com.mrozenblum.poatp

import com.mrozenblum.poatp.storage.ItemStorage
import com.mrozenblum.poatp.storage.TransactionStatus.FAILED
import com.mrozenblum.poatp.storage.TransactionStatus.SUCCESS
import com.mrozenblum.poatp.storage.TransactionStorage
import com.mrozenblum.poatp.storage.UserStorage
import org.springframework.stereotype.Service

@Service
class Service(
    private val transactionStorageService: TransactionStorage,
    private val userStorage: UserStorage,
    private val itemStorageService: ItemStorage,
) {
    fun getUser(userId: Long) = userStorage.search(userId)

    fun saveUser(user: User) = userStorage.store(user)

    fun saveItem(item: Item) = itemStorageService.store(item)

    fun getItem(itemId: Long) = itemStorageService.search(itemId)

    fun createTransaction(transaction: Transaction) {
        var value: Long = 0
        transaction.items.forEach {
            val item = getItem(it)
            value += item.value
        }
        transactionStorageService.store(transaction.copy(value = value))
    }

    fun getTransaction(transactionId: Long) = transactionStorageService.search(transactionId)

    fun closeTransaction(transactionId: Long) {
        val transaction = getTransaction(transactionId)
        val userPoints = userStorage.getPoints(transaction.userId)
        var status = FAILED
        if (userPoints > transaction.value!!) {
            status = SUCCESS
            userStorage.discountPoints(transaction.userId, userPoints - transaction.value)
        }
        transactionStorageService.updateStatus(transactionId, status)

    }
}