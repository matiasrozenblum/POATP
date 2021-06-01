package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.Transaction
import com.mrozenblum.poatp.TransactionNotFoundException
import org.assertj.core.api.Assertions
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
class TransactionStorageTest {
    @Autowired
    lateinit var transactionStorage: TransactionStorage

    @AfterEach
    fun cleanUp() {
        transaction { SchemaUtils.drop(TransactionTable) }
        transaction { SchemaUtils.create(TransactionTable) }
    }

    @Test
    fun testStoreAndSearch() {
        assertThrows<TransactionNotFoundException> {
            transactionStorage.search(1)
        }

        val transaction = Transaction(
            userId = 1,
            items = listOf(1),
            value = 10
        )
        transactionStorage.store(transaction)

        transactionStorage.search(1).apply {
            Assertions.assertThat(this).isNotNull
            Assertions.assertThat(this.copy(id = null, status = null)).isEqualTo(transaction)
        }
    }

    @Test
    fun testDelete() {
        assertThrows<TransactionNotFoundException> {
            transactionStorage.search(1)
        }

        val transaction = Transaction(
            userId = 1,
            items = listOf(1),
            value = 10
        )
        transactionStorage.store(transaction)

        transactionStorage.search(1).apply {
            Assertions.assertThat(this).isNotNull
            Assertions.assertThat(this.copy(id = null, status = null)).isEqualTo(transaction)
        }

        transactionStorage.delete(1).apply {
            Assertions.assertThat(this).isTrue
        }

        assertThrows<TransactionNotFoundException> {
            transactionStorage.search(1)
        }
    }

    @Test
    fun testDeleteFailedNotFound() {
        assertThrows<TransactionNotFoundException> {
            transactionStorage.search(1)
        }

        transactionStorage.delete(1).apply {
            Assertions.assertThat(this).isFalse
        }
    }
}