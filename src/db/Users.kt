package com.github.odaridavid.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

//TODO Read on database indexes
object Users : Table() {
    val userId: Column<Int> = integer("id").autoIncrement().primaryKey()
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 256)
    val passwordHash = varchar("password_hash", 64)
}
