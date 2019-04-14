package com.example.web_api

import com.example.web_api.model.Samples
import com.example.web_api.service.LibriaryService
import com.example.web_api.service.SampleService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.web_api.web.sample

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val database = initDB()

    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val sampleService = SampleService()
    val libriaryService = LibriaryService()

    install(Routing) {
        sample(sampleService)
    }

    routing {
        route("/repositories") {
            get {
                call.respondText("repositories", contentType = ContentType.Text.Plain)
            }
        }

        route("/libraries") {
            get {
                call.respondText("libraries", contentType = ContentType.Text.Plain)
            }
        }

        route("/request") {
            post {
                call.respondText("request", contentType = ContentType.Text.Plain)
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


fun initDB(): Database {
    val database = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

    transaction {
        // print sql to std-out
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Samples)

        Samples.insert {
            it[lib] = "lib"
            it[name] = "name"
            it[buildSystem] = "buildSystem"
            it[path] = "path"
            it[description] = "description"
            it[tags] = "tags"
        }
    }

    return database
}

