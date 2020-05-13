package com.github.odaridavid.models

import io.ktor.auth.Principal
import java.io.Serializable

//TODO Read up on Principal from https://ktor.io/servers/features/authentication.html
data class User(
    val userId: Int,
    val email: String,
    val displayName: String,
    val passwordHash: String
) : Serializable, Principal