package com.github.odaridavid

import com.github.odaridavid.authentication.JwtService
import com.github.odaridavid.authentication.hash
import com.github.odaridavid.db.DatabaseFactory
import com.github.odaridavid.repositories.UserRepository
import com.github.odaridavid.routes.root
import com.github.odaridavid.routes.todo
import com.github.odaridavid.routes.users
import com.github.odaridavid.sessions.ToDoSession
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.util.KtorExperimentalAPI

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(Sessions) {
        cookie<ToDoSession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    DatabaseFactory.init()
    val userRepo = UserRepository()
    val jwt = JwtService()
    val hashPassword = { s: String -> hash(s) }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwt.verifier)
            realm = "ToDo Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id").asInt()
                val user = userRepo.findUserById(claim)
                user
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        root()
        users()
        todo()
    }
}

const val API_VERSION = "/v1"
