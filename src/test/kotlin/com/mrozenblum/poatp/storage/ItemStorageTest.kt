package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.ItemNotFoundException
import com.mrozenblum.poatp.domain.Item
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
        assertThrows<ItemNotFoundException> {
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
        assertThrows<ItemNotFoundException> {
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

        assertThrows<ItemNotFoundException> {
            itemStorage.search(1)
        }
    }

    @Test
    fun testDeleteFailedNotFound() {
        assertThrows<ItemNotFoundException> {
            itemStorage.search(1)
        }

        itemStorage.delete(1).apply {
            assertThat(this).isFalse
        }
    }
}