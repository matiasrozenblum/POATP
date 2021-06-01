package com.mrozenblum.poatp

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

class UserNotFoundException : RuntimeException("Cannot find user")
class ItemNotFoundException : RuntimeException("Cannot find item")
class TransactionNotFoundException : RuntimeException("Cannot find transaction")

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(value = [UserNotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handle(e: UserNotFoundException): ErrorMessage {
        return errorMessage(e, "user_not_found")
    }

    @ExceptionHandler(value = [ItemNotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handle(e: ItemNotFoundException): ErrorMessage {
        return errorMessage(e, "item_not_found")
    }

    @ExceptionHandler(value = [TransactionNotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handle(e: TransactionNotFoundException): ErrorMessage {
        return errorMessage(e, "transaction_not_found")
    }

    fun errorMessage(exception: Throwable, code: String) = ErrorMessage(exception.message ?: "", code)

    data class ErrorMessage(val message: String, val code: String)
}