package com.example.app

import com.example.app.api.AddRequest
import com.example.app.api.myRoute
import com.example.app.db.*
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    DB.init()

    initSamplesInfo()

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

        myRoute("/jobs", Jobs)
    }

    routing {

        post("/add") {
            val addRequest = call.receive<AddRequest>()
            val jobId = Jobs.insert(JobRow(-1, -1, "None", "", addRequest.toString()))
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

fun initSamplesInfo() {
    val oldInfo = readSamplesInfo()
    val newInfo = buildSamplesInfo()
    if (oldInfo == null || oldInfo != newInfo) {
        writeSamplesInfo(newInfo)
    }
}

fun writeSamplesInfo(samplesInfo: List<RepoInfo>) {
    val json = Json(JsonConfiguration.Stable)
    val sInfo = json.stringify(RepoInfo.serializer().list, samplesInfo)
    Properties.infoFile.outputStream().use {
        it.write(sInfo.toByteArray())
    }
}


fun buildSamplesInfo(): List<RepoInfo> {
    val tmpMap = mutableMapOf<Pair<String, String>, MutableList<SampleInfo>>()
    transaction {
        (Samples leftJoin Snapshots leftJoin Repositories).select {
            Snapshots.status eq SnapshotStatus.OK.name
            Samples.validSnapshotId eq Snapshots.id
        }.forEach {
            val key = it[Repositories.owner] to it[Repositories.repo]
            tmpMap.getOrPut(key) { mutableListOf() }.add(
                SampleInfo(
                    it[Samples.name],
                    it[Snapshots.readme],
                    it[Snapshots.buildSystem],
                    it[Snapshots.sha]
                )
            )
        }
    }

    val res = mutableListOf<RepoInfo>()
    tmpMap.forEach { (owner, repo), samples -> res.add(RepoInfo(owner, repo, samples)) }
    return res
}


fun readSamplesInfo(): List<RepoInfo>? {
    val infoFile = Properties.infoFile
    return if (!infoFile.exists()) {
        null
    } else{
        val json = Json(JsonConfiguration.Stable)
        json.parse(RepoInfo.serializer().list, infoFile.readText())
    }
}

@Serializable
data class RepoInfo(
    val owner: String,
    val repo: String,
    val samples: List<SampleInfo>
)

@Serializable
data class SampleInfo(
    val name: String,
    val readMe: String,
    val buildSystem: String,
    val sha: String
)
