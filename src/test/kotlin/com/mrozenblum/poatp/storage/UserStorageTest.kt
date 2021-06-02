package com.mrozenblum.poatp.storage

import com.mrozenblum.poatp.User
import com.mrozenblum.poatp.UserNotFoundException
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
class UserStorageTest {
    @Autowired
    lateinit var userStorage: UserStorage

    @AfterEach
    fun cleanUp() {
        transaction { SchemaUtils.drop(UserTable) }
        transaction { SchemaUtils.create(UserTable) }
    }

    @Test
    fun testStoreAndSearch() {
        assertThrows<UserNotFoundException> {
            userStorage.search(1)
        }

        val user = User(
            name = "Juan Perez",
            email = "juanperez@gmail.com",
            points = 10
        )
        userStorage.store(user)

        userStorage.search(1).apply {
            assertThat(this).isNotNull
            assertThat(this.copy(null)).isEqualTo(user)
        }
    }

    @Test
    fun testStoreAndSearchByEmail() {
        assertThrows<UserNotFoundException> {
            userStorage.search(1)
        }

        val user = User(
            name = "Juan Perez",
            email = "juanperez@gmail.com",
            points = 10
        )
        userStorage.store(user)

        userStorage.searchByEmail("juanperez@gmail.com").apply {
            assertThat(this).isNotNull
            assertThat(this.copy(null)).isEqualTo(user)
        }
    }

    @Test
    fun testDelete() {
        assertThrows<UserNotFoundException> {
            userStorage.search(1)
        }

        val user = User(
            name = "Juan Perez",
            email = "juanperez@gmail.com",
            points = 10
        )
        userStorage.store(user)

        userStorage.search(1).apply {
            assertThat(this).isNotNull
            assertThat(this.copy(null)).isEqualTo(user)
        }

        userStorage.delete(1).apply {
            assertThat(this).isTrue
        }

        assertThrows<RuntimeException> {
            userStorage.search(1)
        }
    }

    @Test
    fun testDeleteByEmail() {
        assertThrows<UserNotFoundException> {
            userStorage.search(1)
        }

        val user = User(
            name = "Juan Perez",
            email = "juanperez@gmail.com",
            points = 10
        )
        userStorage.store(user)

        userStorage.search(1).apply {
            assertThat(this).isNotNull
            assertThat(this.copy(null)).isEqualTo(user)
        }

        userStorage.deleteByEmail("juanperez@gmail.com").apply {
            assertThat(this).isTrue
        }

        assertThrows<RuntimeException> {
            userStorage.search(1)
        }
    }

    @Test
    fun testDeleteFailedNotFound() {
        assertThrows<UserNotFoundException> {
            userStorage.search(1)
        }

        userStorage.delete(1).apply {
            assertThat(this).isFalse
        }
    }
}