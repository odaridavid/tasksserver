package com.github.odaridavid.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(provideHikariDataSource())

        //Create if dont exist
        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Tasks)
        }
    }

    private fun provideHikariDataSource(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER")
        config.jdbcUrl = System.getenv("DATABASE_URL")
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        //Needed when deployed online e.g heroku or gcp
        val user = System.getenv("DB_USER")
        user?.run {
            config.username = user
        }
        val password = System.getenv("DB_PASSWORD")
        password?.run {
            config.password = password
        }
        config.validate()
        return HikariDataSource(config)
    }

}