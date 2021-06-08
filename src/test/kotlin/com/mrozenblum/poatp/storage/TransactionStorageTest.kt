package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.TransactionNotFoundException
import com.mrozenblum.poatp.domain.Item
import com.mrozenblum.poatp.domain.TransactionBody
import com.mrozenblum.poatp.domain.TransactionItem
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class TransactionStorageTest {

    @Mock
    lateinit var itemStorage: ItemStorage

    @Mock
    lateinit var transactionItemStorage: TransactionItemStorage

    lateinit var transactionStorage: TransactionStorage

    @BeforeEach
    fun setUp() {
        transactionStorage = TransactionStorage(itemStorage, transactionItemStorage)
    }

    @AfterEach
    fun cleanUp() {
        transaction { SchemaUtils.drop(TransactionTable, ItemTable, TransactionItemTable) }
        transaction { SchemaUtils.create(TransactionTable, ItemTable, TransactionItemTable) }
    }

    @Test
    fun testStoreAndSearch() {
        assertThrows<TransactionNotFoundException> {
            transactionStorage.search(1)
        }

        val transactionBody = TransactionBody(
            userId = 1,
            items = listOf(1),
            value = 10
        )
        val item = Item(
            1,
            "pelota",
            10
        )
        val transactionItem = TransactionItem(transactionId = 1, itemId = 1)
        transactionStorage.store(transactionBody)
        whenever(transactionItemStorage.searchByTransactionId(1)).thenReturn(listOf(transactionItem))
        whenever(itemStorage.searchByIdList(listOf(transactionItem))).thenReturn(listOf(item))
        transactionStorage.search(1).apply {
            Assertions.assertThat(this).isNotNull
            Assertions.assertThat(this.userId).isEqualTo(1)
            Assertions.assertThat(this.value).isEqualTo(10)
        }
    }

    @Test
    fun testDelete() {
        assertThrows<TransactionNotFoundException> {
            transactionStorage.search(1)
        }

        val transactionBody = TransactionBody(
            userId = 1,
            items = listOf(1),
            value = 10
        )
        val item = Item(
            1,
            "pelota",
            10
        )
        val transactionItem = TransactionItem(transactionId = 1, itemId = 1)
        transactionStorage.store(transactionBody)
        whenever(transactionItemStorage.searchByTransactionId(1)).thenReturn(listOf(transactionItem))
        whenever(itemStorage.searchByIdList(listOf(transactionItem))).thenReturn(listOf(item))
        transactionStorage.search(1).apply {
            Assertions.assertThat(this).isNotNull
            Assertions.assertThat(this.userId).isEqualTo(1)
            Assertions.assertThat(this.value).isEqualTo(10)
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