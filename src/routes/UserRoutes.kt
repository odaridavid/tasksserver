package com.github.odaridavid.routes


import com.github.odaridavid.Constants.Keys.DISPLAY_NAME
import com.github.odaridavid.Constants.Keys.EMAIL
import com.github.odaridavid.Constants.Keys.PASSWORD
import com.github.odaridavid.Constants.Routes.USER_LOGIN
import com.github.odaridavid.Constants.Routes.USER_SIGN_UP
import com.github.odaridavid.authentication.JwtService
import com.github.odaridavid.repositories.IUserRepository
import com.github.odaridavid.sessions.TaskSession
import com.github.odaridavid.utils.*
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.sessions.sessions
import io.ktor.sessions.set


//TODO Validate email by sending confirm email

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_SIGN_UP)
class UserSignUpRoute

@KtorExperimentalLocationsAPI
fun Route.users(userRepo: IUserRepository, jwtService: JwtService, hashBlock: (String) -> String) {

    signup(hashBlock, userRepo, jwtService)

    login(hashBlock, userRepo, jwtService)

}

@KtorExperimentalLocationsAPI
private fun Route.login(hashBlock: (String) -> String, userRepo: IUserRepository, jwtService: JwtService) {

    post<UserLoginRoute> {

        val loginParams = call.receive<Parameters>()

        val password = loginParams[PASSWORD] ?: return@post respondWithUnauthorized("Missing Password Field")
        val email = loginParams[EMAIL] ?: return@post respondWithUnauthorized("Missing Email Field")
        val hashedPassword = hashBlock(password)

        performRequest("User Login") {

            val user = userRepo.findUserByEmail(email)

            user?.run {
                if (user.passwordHash == hashedPassword) {
                    call.sessions.set(TaskSession(userId))
                    val token = jwtService.generateToken(user)
                    respondWithOk(token)
                } else {
                    respondWithUnauthorized("Invalid Credentials")
                }
            }
        }
    }

}

@KtorExperimentalLocationsAPI
private fun Route.signup(
    hashBlock: (String) -> String,
    userRepo: IUserRepository,
    jwtService: JwtService
) {

    post<UserSignUpRoute> {

        val signUpParams = call.receive<Parameters>()

        val password = signUpParams[PASSWORD] ?: return@post respondWithUnauthorized("Missing Password Field")
        val displayName =
            signUpParams[DISPLAY_NAME] ?: return@post respondWithUnauthorized("Missing Display Name Field")
        val email = signUpParams[EMAIL] ?: return@post respondWithUnauthorized("Missing Email Field")

        with(Validator) {
            if (!isValidPassword(password)) return@post respondWithBadRequest("Check On Password Field")
            if (!isValidDisplayName(displayName)) return@post respondWithBadRequest("Display Name Can't Be Empty")
            if (!isValidEmail(email)) return@post respondWithBadRequest("Invalid Email Provided")
        }

        val hashedPassword = hashBlock(password)

        performRequest("User SignUp") {

            val user = userRepo.createUser(email, displayName, hashedPassword)

            user?.run {
                call.sessions.set(TaskSession(userId))
                val token = jwtService.generateToken(user)
                respondWithCreated(token)
            } ?: respondWithBadRequest("Error Creating User")
        }
    }

}


