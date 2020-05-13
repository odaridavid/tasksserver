package com.github.odaridavid.repositories

import com.github.odaridavid.db.Users
import com.github.odaridavid.models.User
import com.github.odaridavid.utils.executeDbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class UserRepository : IUserRepository {

    override suspend fun createUser(email: String, displayName: String, passwordHash: String): User? {
        var statement: InsertStatement<Number>? = null
        executeDbQuery {
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.displayName] = displayName
                user[Users.passwordHash] = passwordHash
            }
        }
        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUserById(userId: Int): User? {
        return executeDbQuery {
            Users.select { Users.userId eq userId }
                .map { row -> rowToUser(row) }
                .singleOrNull()
        }
    }

    override suspend fun findUserByEmail(email: String): User? {
        return executeDbQuery {
            Users.select { Users.email eq email }
                .map { row -> rowToUser(row) }
                .singleOrNull()
        }
    }

    private fun rowToUser(row: ResultRow?): User? {
        return row?.run {
            User(
                userId = row[Users.userId],
                email = row[Users.email],
                displayName = row[Users.displayName],
                passwordHash = row[Users.passwordHash]
            )
        }
    }

}