package com.github.odaridavid.routes

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get

//TODO Show a 404 Template
fun Route.root() {
    get("/") {
        call.respondText("{error:\"Nothing Here\"}", contentType = ContentType.Text.Plain)
    }
}