package com.example.app

import com.example.app.api.AddRequest
import com.example.app.db.JobService
import com.example.app.db.Repositories
import com.example.app.db.Samples
import com.example.app.db.Snapshots
import com.fasterxml.jackson.databind.PropertyNamingStrategy
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
import io.ktor.routing.*
import kotlinx.coroutines.async
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

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
        route("/all") {
            val res = mutableMapOf<Pair<String, String>, MutableList<Map<String, Any>>>()
            transaction {
                (Samples leftJoin Snapshots leftJoin Repositories).selectAll()
                    .forEach {
                        val key = it[Repositories.owner] to it[Repositories.repo]
                        res.getOrPut(key) { mutableListOf() }.add(
                            mapOf(
                                "name" to it[Samples.name],
                                "readme" to it[Snapshots.readme]
                            )
                        )
                    }
            }
        }
//        myRoute("/branches", SnapshotService)
//        myRoute("/files", FileService)
//        myRoute("/libraries", LibraryService)
//        myRoute("/repositories", RepositoryService)
//        myRoute("/samples", SampleService)
    }

    routing {

        post("/add") {
            val addRequest = call.receive<AddRequest>()
            val jobId = JobService.create(addRequest.toString())
            async { Checker.checker.execute(CheckerContext(jobId, addRequest), Unit) }
            call.respond(mapOf("id" to jobId))
        }

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

