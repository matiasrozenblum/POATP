package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.TransactionItemNotFoundException
import com.mrozenblum.poatp.domain.TransactionItem
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class TransactionItemStorageTest {
    @Autowired
    lateinit var transactionItemStorage: TransactionItemStorage

    @AfterEach
    fun cleanUp() {
        transaction { SchemaUtils.drop(TransactionItemTable) }
        transaction { SchemaUtils.create(TransactionItemTable) }
    }

    @Test
    fun testStoreAndSearch() {
        assertThrows<TransactionItemNotFoundException> {
            transactionItemStorage.search(1)
        }

        val transactionItem = TransactionItem(
            transactionId = 1,
            itemId = 1
        )
        transactionItemStorage.store(transactionItem)

        transactionItemStorage.search(1).apply {
            assertThat(this).isNotNull
            assertThat(this.copy(null)).isEqualTo(transactionItem)
        }
    }

    @Test
    fun testStoreAndSearchByTransactionId() {
        assertThrows<TransactionItemNotFoundException> {
            transactionItemStorage.searchByTransactionId(1)
        }

        val transactionItem = TransactionItem(
            transactionId = 1,
            itemId = 1
        )
        transactionItemStorage.store(transactionItem)

        transactionItemStorage.searchByTransactionId(1).apply {
            assertThat(this).isNotEmpty
            assertThat(this[0].copy(null)).isEqualTo(transactionItem)
        }
    }

    @Test
    fun testDelete() {
        assertThrows<TransactionItemNotFoundException> {
            transactionItemStorage.search(1)
        }

        val transactionItem = TransactionItem(
            transactionId = 1,
            itemId = 1
        )
        transactionItemStorage.store(transactionItem)

        transactionItemStorage.search(1).apply {
            assertThat(this).isNotNull
            assertThat(this.copy(null)).isEqualTo(transactionItem)
        }

        transactionItemStorage.delete(1).apply {
            assertThat(this).isTrue
        }

        assertThrows<TransactionItemNotFoundException> {
            transactionItemStorage.search(1)
        }
    }

    @Test
    fun testDeleteByTransactionId() {
        assertThrows<TransactionItemNotFoundException> {
            transactionItemStorage.searchByTransactionId(1)
        }

        val transactionItem = TransactionItem(
            transactionId = 1,
            itemId = 1
        )
        transactionItemStorage.store(transactionItem)

        transactionItemStorage.searchByTransactionId(1).apply {
            assertThat(this).isNotEmpty
            assertThat(this[0].copy(null)).isEqualTo(transactionItem)
        }

        transactionItemStorage.deleteByTransactionId(1).apply {
            assertThat(this).isTrue
        }

        assertThrows<TransactionItemNotFoundException> {
            transactionItemStorage.search(1)
        }
    }

    @Test
    fun testDeleteFailedNotFound() {
        assertThrows<TransactionItemNotFoundException> {
            transactionItemStorage.search(1)
        }

        transactionItemStorage.delete(1).apply {
            assertThat(this).isFalse
        }
    }
}