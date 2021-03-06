package com.mrozenblum.poatp.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mrozenblum.poatp.domain.Item
import com.mrozenblum.poatp.domain.TransactionBody
import com.mrozenblum.poatp.domain.User
import com.mrozenblum.poatp.storage.ItemTable
import com.mrozenblum.poatp.storage.TransactionItemTable
import com.mrozenblum.poatp.storage.TransactionStatus.*
import com.mrozenblum.poatp.storage.TransactionTable
import com.mrozenblum.poatp.storage.UserTable
import org.hamcrest.Matchers
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ApiControllerTest(@Autowired private val mockMvc: MockMvc) {

    @AfterEach
    fun cleanUp() {
        transaction { SchemaUtils.drop(TransactionTable, UserTable, ItemTable, TransactionItemTable) }
        transaction { SchemaUtils.create(TransactionTable, UserTable, ItemTable, TransactionItemTable) }
    }

    @Test
    fun createUser() {
        val user = User(
            name = "Juan Perez",
            email = "juanperez@gmail.com",
            points = 10
        )

        mockMvc
            .perform(
                post("/api/user")
                    .content(jacksonObjectMapper().writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
    }

    @Test
    fun getUser() {
        saveUser()
        mockMvc
            .perform(
                get("/api/user/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Juan Perez"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("juanperez@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(10))
    }

    @Test
    fun getUserByEmail() {
        saveUser()
        mockMvc
            .perform(
                get("/api/user?email=juanperez@gmail.com")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Juan Perez"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("juanperez@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(10))
    }

    @Test
    fun deleteUser() {
        saveUser()
        mockMvc
            .perform(
                delete("/api/user/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun getUserNotFound() {
        mockMvc
            .perform(
                get("/api/user/2")
            )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("user_not_found")))
    }

    @Test
    fun deleteUserNotFound() {
        mockMvc
            .perform(
                delete("/api/user/2")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun createItem() {
        val item = Item(
            name = "Pelota",
            value = 10
        )

        mockMvc
            .perform(
                post("/api/item")
                    .content(jacksonObjectMapper().writeValueAsString(item))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.itemId").value(1))
    }

    @Test
    fun getItem() {
        saveItem()
        mockMvc
            .perform(
                get("/api/item/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Pelota"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(10))
    }

    @Test
    fun deleteItem() {
        saveItem()
        mockMvc
            .perform(
                delete("/api/item/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun getItemNotFound() {
        mockMvc
            .perform(
                get("/api/item/2")
            )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("item_not_found")))
    }

    @Test
    fun deleteItemNotFound() {
        mockMvc
            .perform(
                delete("/api/item/2")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun createTransaction() {
        saveItem()
        saveUser()
        val transactionBody = TransactionBody(
            userId = 1,
            items = listOf(1),
        )
        mockMvc
            .perform(
                post("/api/transaction")
                    .content(jacksonObjectMapper().writeValueAsString(transactionBody))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").value(1))
    }

    @Test
    fun getTransaction() {
        saveTransaction()
        saveItem()
        saveTransactionItem()
        mockMvc
            .perform(
                get("/api/transaction/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(OPEN.name))
    }

    @Test
    fun deleteTransaction() {
        saveTransaction()
        mockMvc
            .perform(
                delete("/api/transaction/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun getTransactionNotFound() {
        mockMvc
            .perform(
                get("/api/transaction/2")
            )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("transaction_item_not_found")))
    }

    @Test
    fun deleteTransactionNotFound() {
        mockMvc
            .perform(
                delete("/api/transaction/2")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun closeTransactionWithEnoughPoints() {
        saveUser()
        saveTransaction()
        saveItem()
        saveTransactionItem()
        mockMvc
            .perform(
                put("/api/transaction/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("true")))

        mockMvc
            .perform(
                get("/api/user/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Juan Perez"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("juanperez@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(0))

        mockMvc
            .perform(
                get("/api/transaction/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(SUCCESS.name))
    }

    @Test
    fun closeTransactionWithoutEnoughPoints() {
        saveUser(5)
        saveTransaction()
        saveItem()
        saveTransactionItem()
        mockMvc
            .perform(
                put("/api/transaction/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("true")))

        mockMvc
            .perform(
                get("/api/user/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Juan Perez"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("juanperez@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(5))

        mockMvc
            .perform(
                get("/api/transaction/1")
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.items").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.value").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(FAILED.name))
    }

    private fun saveUser(value: Long = 10) {
        transaction {
            UserTable.insert {
                it[name] = "Juan Perez"
                it[email] = "juanperez@gmail.com"
                it[points] = value
            }
        }
    }

    private fun saveItem() {
        transaction {
            ItemTable.insert {
                it[name] = "Pelota"
                it[value] = 10
            }
        }
    }

    private fun saveTransaction() {
        transaction {
            TransactionTable.insert {
                it[userId] = 1
                it[value] = 10
            }
        }
    }

    private fun saveTransactionItem() {
        transaction {
            TransactionItemTable.insert {
                it[transactionId] = 1
                it[itemId] = 1
            }
        }
    }
}