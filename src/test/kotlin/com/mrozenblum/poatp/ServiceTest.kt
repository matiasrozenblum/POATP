package com.mrozenblum.poatp

import com.mrozenblum.poatp.storage.ItemStorage
import com.mrozenblum.poatp.storage.TransactionStatus.*
import com.mrozenblum.poatp.storage.TransactionStorage
import com.mrozenblum.poatp.storage.UserStorage
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ServiceTest {
    @Mock
    private lateinit var userStorage: UserStorage

    @Mock
    private lateinit var itemStorage: ItemStorage

    @Mock
    private lateinit var transactionStorage: TransactionStorage

    private lateinit var service: Service

    @BeforeEach
    fun setUp() {
        service = Service(transactionStorage, userStorage, itemStorage)
    }

    @Test
    fun getUser() {
        val user = User(
            name = "Juan Perez",
            email = "juanperez@gmail.com",
            points = 10
        )
        whenever(userStorage.search(1)).thenReturn(user)
        service.getUser(1).apply {
            assertThat(this.copy(null)).isEqualTo(user)
        }
    }

    @Test
    fun getUserByEmail() {
        val user = User(
            name = "Juan Perez",
            email = "juanperez@gmail.com",
            points = 10
        )
        whenever(userStorage.searchByEmail("juanperez@gmail.com")).thenReturn(user)
        service.getUserByEmail("juanperez@gmail.com").apply {
            assertThat(this.copy(null)).isEqualTo(user)
        }
    }

    @Test
    fun saveUser() {
        val user = User(
            name = "Juan Perez",
            email = "juanperez@gmail.com",
            points = 10
        )
        val userResponse = UserResponse(1)
        whenever(userStorage.store(user)).thenReturn(userResponse)

        service.saveUser(user).apply {
            assertThat(this).isEqualTo(userResponse)
        }
    }

    @Test
    fun deleteUser() {
        whenever(userStorage.delete(1)).thenReturn(true)

        userStorage.delete(1).apply {
            assertThat(this).isTrue
        }
    }

    @Test
    fun deleteUserByEmail() {
        whenever(userStorage.deleteByEmail("juanperez@gmail.com")).thenReturn(true)

        userStorage.deleteByEmail("juanperez@gmail.com").apply {
            assertThat(this).isTrue
        }
    }

    @Test
    fun getItem() {
        val item = Item(
            name = "pelota",
            value = 10
        )
        whenever(itemStorage.search(1)).thenReturn(item)
        service.getItem(1).apply {
            assertThat(this.copy(null)).isEqualTo(item)
        }
    }

    @Test
    fun saveItem() {
        val item = Item(
            name = "pelota",
            value = 10
        )
        val itemResponse = ItemResponse(1)
        whenever(itemStorage.store(item)).thenReturn(itemResponse)

        service.saveItem(item).apply {
            assertThat(this).isEqualTo(itemResponse)
        }
    }

    @Test
    fun deleteItem() {
        whenever(itemStorage.delete(1)).thenReturn(true)

        itemStorage.delete(1).apply {
            assertThat(this).isTrue
        }
    }

    @Test
    fun createTransaction() {
        val transaction = Transaction(
            userId = 1,
            items = listOf(1)
        )
        val item = Item(
            1,
            "pelota",
            10
        )
        val transactionResponse = TransactionResponse(1)
        whenever(itemStorage.search(1)).thenReturn(item)
        whenever(transactionStorage.store(transaction.copy(value = 10))).thenReturn(transactionResponse)

        service.createTransaction(transaction).apply {
            assertThat(this).isEqualTo(transactionResponse)
        }
    }

    @Test
    fun getTransaction() {
        val transaction = Transaction(
            userId = 1,
            items = listOf(1),
            value = 10,
            status = OPEN.name
        )
        whenever(transactionStorage.search(1)).thenReturn(transaction)
        service.getTransaction(1).apply {
            assertThat(this.copy(null)).isEqualTo(transaction)
        }
    }

    @Test
    fun deleteTransaction() {
        whenever(transactionStorage.delete(1)).thenReturn(true)

        transactionStorage.delete(1).apply {
            assertThat(this).isTrue
        }
    }

    @Test
    fun closeTransactionWithEnoughPoints() {
        val transaction = Transaction(
            1,
            1,
            listOf(1),
            10,
            OPEN.name
        )
        whenever(transactionStorage.search(1)).thenReturn(transaction)
        whenever(userStorage.getPoints(1)).thenReturn(10)
        whenever(userStorage.discountPoints(1, 0)).thenReturn(true)
        whenever(transactionStorage.updateStatus(1, SUCCESS)).thenReturn(true)

        service.closeTransaction(1).apply {
            assertThat(this).isTrue
        }
    }

    @Test
    fun closeTransactionWithoutEnoughPoints() {
        val transaction = Transaction(
            1,
            1,
            listOf(1),
            10,
            OPEN.name
        )
        whenever(transactionStorage.search(1)).thenReturn(transaction)
        whenever(userStorage.getPoints(1)).thenReturn(5)
        whenever(transactionStorage.updateStatus(1, FAILED)).thenReturn(true)

        service.closeTransaction(1).apply {
            assertThat(this).isTrue
        }
    }
}