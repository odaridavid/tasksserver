package com.github.odaridavid

import com.github.odaridavid.authentication.JwtService
import com.github.odaridavid.authentication.hash
import com.github.odaridavid.db.DatabaseFactory
import com.github.odaridavid.repositories.TaskRepository
import com.github.odaridavid.repositories.UserRepository
import com.github.odaridavid.routes.root
import com.github.odaridavid.routes.tasks
import com.github.odaridavid.routes.users
import com.github.odaridavid.sessions.TaskSession
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.KtorExperimentalAPI
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(Locations)

    install(Sessions) {
        cookie<TaskSession>("TASK_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    //Data
    DatabaseFactory.init()
    val userRepo = UserRepository()
    val taskRepo = TaskRepository()

    //Security
    val jwt = JwtService()
    val hashPassword = { s: String -> hash(s) }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwt.verifier)
            realm = environment.config.property("jwt.realm").getString()
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
        users(userRepo, jwt, hashPassword)
        tasks(taskRepo, userRepo)
    }
}

