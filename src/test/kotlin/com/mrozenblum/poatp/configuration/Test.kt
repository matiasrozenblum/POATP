package com.mrozenblum.poatp.configuration

import com.mrozenblum.poatp.storage.ItemTable
import com.mrozenblum.poatp.storage.TransactionTable
import com.mrozenblum.poatp.storage.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class TestConfig {

    @Bean
    fun tpDB() = Database.connect("jdbc:h2:mem:testBR;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        .also {
            transaction {
                SchemaUtils.create(TransactionTable, UserTable, ItemTable)
            }
        }
}
