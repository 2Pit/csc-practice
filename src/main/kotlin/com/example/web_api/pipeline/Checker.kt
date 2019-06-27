package com.example.web_api.pipeline

import com.example.git.Property
import com.example.web_api.new_model.Libraries
import com.example.web_api.new_model.LocationFilter
import com.example.web_api.new_model.Locations
import com.example.web_api.new_model.Repositories
import com.example.web_api.pipeline.git.Connector
import com.example.web_api.pipeline.git.SampleBuilder
import com.example.web_api.pipeline.git.SampleRequest
import com.example.web_api.pipeline.git.raise
import com.example.web_api.service.JobService
import com.example.web_api.service.RepositoryService
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.util.pipeline.Pipeline
import io.ktor.util.pipeline.PipelinePhase
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object Checker {
    private val locationChecker = PipelinePhase("LocationChecker")
    private val sampleChecker = PipelinePhase("SampleChecker")

    val checker = Pipeline<Unit, CheckerContext>(locationChecker, sampleChecker)

    init {
        checker.intercept(locationChecker) {
            val chCtx = this.context
            val location = chCtx.addRequest.location

            val loc = Locations.getBy(
                LocationFilter(
                    owner = location.owner,
                    name = location.name,
                    branch = location.branch,
                    path = location.path
                )
            )
            if (loc.isNotEmpty()) {
                JobService.unpdate(chCtx.jobId, "In progress", "The Location already exist.")
                return@intercept
            }

            val repository = Connector.getRepository(location.owner, location.name)
                .raise { JobService.unpdate(chCtx.jobId, "Err", "No such location.") }
            val content = Connector.getContent(repository, location.path)
                .raise { JobService.unpdate(chCtx.jobId, "Err", "No such location.") }

            transaction {
                Locations.insert {
                    it[owner] = location.owner
                    it[name] = location.name
                    it[branch] = location.branch
                    it[path] = location.path
                }
            }

            JobService.unpdate(chCtx.jobId, "In progress", "The Repository added.")
        }

        checker.intercept(sampleChecker) {
            val chCtx = this.context
            val addRequest = chCtx.addRequest
            val location = addRequest.location

            JobService.unpdate(chCtx.jobId, "In progress", "Sample checking.")
            val sample = SampleBuilder.buildSample(location)


//                TODO: Turn on Jenkins
//                SampleBuilder.write(sampleRequest, sample)
//                notifyJenkins(sampleRequest)
        }
    }

    private suspend fun notifyJenkins(request: SampleRequest) {
        HttpClient(Apache).use {
            it.post<Unit>(port = Property.jenkinsPort, path = "job/item/buildWithParameters") {
                header("Authorization", "Basic ${Property.jenkinsAuth}")
                parameter("token", Property.jenkinsRunTaskToken)
                parameter(
                    "full_sample_path",
                    request.getPathAtLocatRepo()
                )
            }
        }
    }
}

data class CheckerContext(val jobId: Int, val addRequest: AddRequest)

data class AddRequest(
    val location: Location,
    val sampleInfo: SampleInfo
)

data class Location(val owner: String, val name: String, val branch: String, val path: String)

data class SampleInfo(
    val name: String,
    val buildSystem: String
)