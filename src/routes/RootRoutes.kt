package com.github.odaridavid.routes

import com.github.odaridavid.utils.respondWithNotFound
import io.ktor.routing.Route
import io.ktor.routing.get


fun Route.root() {
    get("/") {
        respondWithNotFound("Nothing To See Here")
    }
}