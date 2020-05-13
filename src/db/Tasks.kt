package com.github.odaridavid.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Tasks : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val userId: Column<Int> = integer("user_id").references(Users.userId)
    val task = varchar("task", 512)
    val isDone = bool("is_done")
}