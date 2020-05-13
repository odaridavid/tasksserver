package com.github.odaridavid.repositories

import com.github.odaridavid.models.User

interface IUserRepository {

    suspend fun createUser(
        email: String,
        displayName: String,
        passwordHash: String
    ): User?

    suspend fun findUserById(userId: Int): User?

    suspend fun findUserByEmail(email: String): User?
}