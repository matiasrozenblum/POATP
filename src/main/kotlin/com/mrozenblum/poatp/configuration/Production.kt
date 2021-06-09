package com.mrozenblum.poatp.configuration

import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("production")
class ProductionConfig {

    @Bean
    fun tpDB(): Database {
        val dataSource = getDatasource()
        return Database.connect(dataSource)
    }

}

fun getDatasource(): DataSource {
    val host = "localhost:3306"
    val dbName = "tp"
    val url = "jdbc:mysql://$host/$dbName"
    val username = "root"
    val password = "123456789Aa"
    val dataSource = DataSourceBuilder.create()
        .type(HikariDataSource::class.java)
        .url(url)
        .username(username)
        .password(password)
        .build()

    dataSource.apply {
        minimumIdle = 10
        idleTimeout = 1000
        maximumPoolSize = 40
        connectionTimeout = 1000
        addDataSourceProperty("cachePrepStmts", true)
        addDataSourceProperty("prepStmtCacheSize", 250)
        addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
        addDataSourceProperty("useServerPrepStmts", true)
        addDataSourceProperty("useLocalSessionState", true)
        addDataSourceProperty("rewriteBatchedStatements", true)
        addDataSourceProperty("cacheResultSetMetadata", true)
        addDataSourceProperty("cacheServerConfiguration", true)
        addDataSourceProperty("elideSetAutoCommits", true)
        addDataSourceProperty("maintainTimeStats", false)
    }
    return dataSource
}