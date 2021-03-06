package com.github.odaridavid

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.features.*
import kotlin.test.*
import io.ktor.server.testing.*
import io.ktor.util.KtorExperimentalAPI

class ApplicationTest {

    //TODO More on Testing and setting up testing env

    @KtorExperimentalLocationsAPI
    @KtorExperimentalAPI
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{error:\"Nothing Here\"}", response.content)
            }
        }
    }
}
