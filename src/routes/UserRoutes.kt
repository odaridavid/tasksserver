package com.github.odaridavid.routes


import com.github.odaridavid.Constants.Keys.DISPLAY_NAME
import com.github.odaridavid.Constants.Keys.EMAIL
import com.github.odaridavid.Constants.Keys.PASSWORD
import com.github.odaridavid.Constants.Routes.USER_LOGIN
import com.github.odaridavid.Constants.Routes.USER_SIGN_UP
import com.github.odaridavid.authentication.JwtService
import com.github.odaridavid.repositories.IUserRepository
import com.github.odaridavid.sessions.TaskSession
import com.github.odaridavid.utils.performRequest
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.pipeline.PipelineContext


//TODO Validations
//TODO Respond in JSON Format


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

        val password = loginParams[PASSWORD] ?: return@post respondEmptyFields()
        val email = loginParams[EMAIL] ?: return@post respondEmptyFields()
        val hashedPassword = hashBlock(password)

        performRequest("User Login") {
            val user = userRepo.findUserByEmail(email)
            user?.userId?.let { id ->
                if (user.passwordHash == hashedPassword) {
                    call.sessions.set(TaskSession(id))
                    call.respondText(jwtService.generateToken(user))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Credentials")
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

        val password = signUpParams[PASSWORD] ?: return@post respondEmptyFields()
        val displayName = signUpParams[DISPLAY_NAME] ?: return@post respondEmptyFields()
        val email = signUpParams[EMAIL] ?: return@post respondEmptyFields()
        val hashedPassword = hashBlock(password)

        performRequest("User SignUp") {
            val user = userRepo.createUser(email, displayName, hashedPassword)
            user?.userId?.let { id ->
                call.sessions.set(TaskSession(id))
                call.respondText(jwtService.generateToken(user), status = HttpStatusCode.Created)
            }
        }
    }

}

private suspend fun PipelineContext<Unit, ApplicationCall>.respondEmptyFields() {
    call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
}

