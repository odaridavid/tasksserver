package com.github.odaridavid.utils

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
        call.respond(HttpStatusCode.BadRequest, "$request Failed")
    }
}