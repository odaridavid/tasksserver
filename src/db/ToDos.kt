package com.github.odaridavid.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ToDos : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val userId: Column<Int> = integer("user_id").references(Users.userId)
    val todo = varchar("todo", 512)
    val done = bool("done")
}