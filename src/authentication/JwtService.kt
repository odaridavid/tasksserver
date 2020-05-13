package com.github.odaridavid.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.github.odaridavid.models.User
import java.util.*

class JwtService(
    private val issuer: String = "todoServer",
    private val jwtSecret: String = System.getenv("JWT_SECRET"),
    private val algorithm: Algorithm = Algorithm.HMAC512(jwtSecret)
) {
    val verifier: JWTVerifier by lazy {
        JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
    }

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", user.userId)
            .withExpiresAt(expiresAt())
            .sign(algorithm)
    }

    private fun expiresAt(): Date? {
        return Date(System.currentTimeMillis() + 3_600_000 * 24)
    }


}