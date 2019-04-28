package com.example.web_api

import com.example.web_api.model.AddRequest
import com.example.web_api.pipeline.Checker
import com.example.web_api.service.*
import com.example.web_api.web.myRoute
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    DB.init()

    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Routing) {
        myRoute("/branches", BranchService)
        myRoute("/files", FileService)
        myRoute("/libraries", LibraryService)
        myRoute("/repositories", RepositoryService)
        myRoute("/samples", SampleService)
    }

    routing {

        post("/add") {
            val add = call.receive<AddRequest>()
            if (((add.repoId == null) xor (add.repo == null)) && ((add.libId == null) xor (add.lib == null))) {
                Checker.checker.execute(add, Unit)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
            call.respond(HttpStatusCode.OK)
        }

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

