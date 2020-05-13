package com.github.odaridavid.authentication

import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@KtorExperimentalAPI
val hashKey = hex(System.getenv("SECRET_KEY"))

@KtorExperimentalAPI
val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

@KtorExperimentalAPI
fun hash(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")//Gets algo to be used for encryption
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}