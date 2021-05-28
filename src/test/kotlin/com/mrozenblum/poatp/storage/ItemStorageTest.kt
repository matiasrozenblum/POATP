package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.Item
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
class ItemStorageTest {
    @Autowired
    lateinit var itemStorage: ItemStorage

    @AfterEach
    fun cleanUp() {
        transaction { SchemaUtils.drop(ItemTable) }
        transaction { SchemaUtils.create(ItemTable) }
    }

    @Test
    fun testStoreAndSearch() {
        assertThrows<RuntimeException> {
            itemStorage.search(1)
        }

        val item = Item(
            name = "pelota",
            value = 10
        )
        itemStorage.store(item)

        itemStorage.search(1).apply {
            assertThat(this).isNotNull
            assertThat(this.copy(null)).isEqualTo(item)
        }
    }

    @Test
    fun testDelete() {
        assertThrows<RuntimeException> {
            itemStorage.search(1)
        }

        val item = Item(
            name = "pelota",
            value = 10
        )
        itemStorage.store(item)

        itemStorage.search(1).apply {
            assertThat(this).isNotNull
            assertThat(this.copy(null)).isEqualTo(item)
        }

        itemStorage.delete(1).apply {
            assertThat(this).isTrue
        }

        assertThrows<RuntimeException> {
            itemStorage.search(1)
        }
    }

    @Test
    fun testDeleteFailedNotFound() {
        assertThrows<RuntimeException> {
            itemStorage.search(1)
        }

        itemStorage.delete(1).apply {
            assertThat(this).isFalse
        }
    }
}