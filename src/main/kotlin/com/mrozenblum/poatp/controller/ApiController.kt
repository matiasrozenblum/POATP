package com.mrozenblum.poatp.controller

import com.mrozenblum.poatp.Item
import com.mrozenblum.poatp.Service
import com.mrozenblum.poatp.Transaction
import com.mrozenblum.poatp.User
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class ApiController(
    private val service: Service
) {

    @PostMapping("/api/user")
    @ResponseStatus(code = HttpStatus.CREATED)
    fun createUser(@RequestBody user: User) = service.saveUser(user)

    @GetMapping("/api/user/{userId}")
    fun getUser(
        @PathVariable userId: Long,
    ) = service.getUser(userId)

    @GetMapping("/api/user")
    fun getUserByEmail(
        @RequestParam email: String,
    ) = service.getUserByEmail(email)

    @DeleteMapping("/api/user/{userId}")
    @ResponseBody
    fun deleteUser(
        @PathVariable userId: Long,
    ) = service.deleteUser(userId)

    @DeleteMapping("/api/user")
    @ResponseBody
    fun deleteUserByEmail(
        @RequestParam email: String,
    ) = service.deleteUserByEmail(email)

    @PostMapping("/api/item")
    @ResponseStatus(code = HttpStatus.CREATED)
    fun createItem(@RequestBody item: Item) = service.saveItem(item)

    @GetMapping("/api/item/{itemId}")
    fun getItem(
        @PathVariable itemId: Long,
    ) = service.getItem(itemId)

    @DeleteMapping("/api/item/{itemId}")
    @ResponseBody
    fun deleteItem(
        @PathVariable itemId: Long,
    ) = service.deleteItem(itemId)

    @PostMapping("/api/transaction")
    @ResponseStatus(code = HttpStatus.CREATED)
    fun createTransaction(@RequestBody transaction: Transaction) = service.createTransaction(transaction)

    @GetMapping("/api/transaction/{transactionId}")
    fun getTransaction(
        @PathVariable transactionId: Long,
    ) = service.getTransaction(transactionId)

    @DeleteMapping("/api/transaction/{transactionId}")
    @ResponseBody
    fun deleteTransaction(
        @PathVariable transactionId: Long,
    ) = service.deleteTransaction(transactionId)

    @PutMapping("/api/transaction/{transactionId}")
    fun closeTransaction(
        @PathVariable transactionId: Long,
    ) = service.closeTransaction(transactionId)
}