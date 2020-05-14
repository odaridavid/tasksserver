package com.github.odaridavid.utils

import com.github.odaridavid.models.ResponseError
import com.github.odaridavid.models.ResponseSuccess
import io.ktor.application.ApplicationCall
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

suspend fun PipelineContext<Unit, ApplicationCall>.performRequest(request: String, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        application.log.error("$request Failed", e)
        respondWithBadRequest("$request Failed")
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.respondWithBadRequest(errorMessage: String) {
    call.respond(HttpStatusCode.BadRequest, ResponseError(errorMessage))
}

suspend fun <T> PipelineContext<Unit, ApplicationCall>.respondWithOk(data: T) {
    call.respond(HttpStatusCode.OK, ResponseSuccess(data))
}

suspend fun PipelineContext<Unit, ApplicationCall>.respondWithNotFound(message: String) {
    call.respond(HttpStatusCode.NotFound, ResponseError(message))
}

suspend fun <T> PipelineContext<Unit, ApplicationCall>.respondWithCreated(data: T) {
    call.respond(HttpStatusCode.Created, ResponseSuccess(data))
}

suspend fun PipelineContext<Unit, ApplicationCall>.respondWithUnauthorized(message: String) {
    call.respond(HttpStatusCode.Unauthorized, ResponseError(message))
}