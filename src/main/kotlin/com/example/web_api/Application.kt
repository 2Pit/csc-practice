package com.example.web_api

import com.example.web_api.model.Branch
import com.example.web_api.service.*
import com.example.web_api.web.myRoute
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
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
        myRoute("/branches", BranchService())
        myRoute("/files", FileService())
        myRoute("/libraries", LibraryService())
        myRoute("/repositories", RepositoryService())
        myRoute("/samples", SampleService())
    }

    routing {

        post("/add") {
            val add = call.receive<Map<String, Any>>()
            val rep = when (val repo = add["repo"]) {
                is Int -> RepositoryService().getById(repo)
                else -> RepositoryService().getById(1)
            }
            val request = call.request
        }

        post("/test/branch") {
            val branch = call.receive<Branch>()
            val i = 9
        }

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

