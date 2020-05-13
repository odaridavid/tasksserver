package com.github.odaridavid.routes

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

@KtorExperimentalLocationsAPI
@Location("route")
class CreateToDoRoute

@KtorExperimentalLocationsAPI
@Location("route")
class DeleteToDoRoute


fun Route.todo() {
//TODO Map ToDo Routes
}