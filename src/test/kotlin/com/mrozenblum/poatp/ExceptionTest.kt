package com.mrozenblum.poatp

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExceptionHandlerTest {

    @Test
    fun handleUserNotFound() {
        ExceptionHandler().handle(UserNotFoundException())
            .apply {
                assertThat(message).isEqualTo("Cannot find user")
                assertThat(code).isEqualTo("user_not_found")
            }
    }

    @Test
    fun handleItemNotFound() {
        ExceptionHandler().handle(ItemNotFoundException())
            .apply {
                assertThat(message).isEqualTo("Cannot find item")
                assertThat(code).isEqualTo("item_not_found")
            }
    }

    @Test
    fun handleTransactionNotFound() {
        ExceptionHandler().handle(TransactionNotFoundException())
            .apply {
                assertThat(message).isEqualTo("Cannot find transaction")
                assertThat(code).isEqualTo("transaction_not_found")
            }
    }
}