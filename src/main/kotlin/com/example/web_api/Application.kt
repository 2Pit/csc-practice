package com.example.web_api

import com.example.web_api.model.AddRequest
import com.example.web_api.pipeline.Checker
import com.example.web_api.service.*
import com.example.web_api.web.myRoute
import com.fasterxml.jackson.databind.PropertyNamingStrategy
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
import kotlinx.coroutines.async

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
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
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
            val addRequest = call.receive<AddRequest>()
            if (((addRequest.repoId == null) xor (addRequest.repo == null))
                && ((addRequest.libId == null) xor (addRequest.lib == null))
            ) {
                val jobId = JobService.create(addRequest.toString())
                async { Checker.checker.execute(addRequest, Unit) }
                call.respond(mapOf("id" to jobId))
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

