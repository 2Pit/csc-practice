package com.example.app

import com.example.app.api.AddRequest
import com.example.app.db.JobService
import com.example.app.db.RepositoryFilter
import com.example.git.Property
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.util.pipeline.Pipeline
import io.ktor.util.pipeline.PipelinePhase
import arrow.core.extensions.`try`.monad.binding
import arrow.core.getOrElse
import com.example.app.db.Repositories
import com.example.app.git.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.transaction

object Checker {
    private val locationChecker = PipelinePhase("LocationChecker")
    private val sampleChecker = PipelinePhase("SampleChecker")
    private val samplePublisher = PipelinePhase("SamplePublisher")

    val checker = Pipeline<Unit, CheckerContext>(
        locationChecker,
        sampleChecker,
        samplePublisher
    )

    init {
        checker.intercept(locationChecker) {
            val chCtx = this.context
            val location = chCtx.addRequest.location

            val repositories = Repositories.getBy(
                RepositoryFilter(
                    owner = location.owner,
                    repo = location.repo,
                    branch = location.branch
                )
            )
            if (repositories.isNotEmpty()) {
                JobService.unpdate(chCtx.jobId, "In progress", "The Location already exist.")
                return@intercept
            }

            binding {
                val (repository) = Connector.getRepository(location.owner, location.repo)
                val (content) = Connector.getContent(repository, location.path)
            }.getOrElse { ex ->
                JobService.unpdate(chCtx.jobId, "Err", "No such location.")
                throw ex
            }

            transaction {
                Repositories.insert {
                    it[owner] = location.owner
                    it[repo] = location.repo
                    it[branch] = location.branch
                }
            }

            JobService.unpdate(chCtx.jobId, "In progress", "The Repository added.")
        }

        checker.intercept(sampleChecker) {
            val chCtx = this.context
            val addRequest = chCtx.addRequest
            val location = addRequest.location

            JobService.unpdate(chCtx.jobId, "In progress", "Sample checking.")
            val files = Connector.downloadProjectFiles(location)
//            val zipSampleOutputStream = compress(files)


//                TODO: Turn on Jenkins
//                SampleBuilder.write(sampleRequest, sample)
//                notifyJenkins(sampleRequest)
            this.context.files = files
        }

        checker.intercept(samplePublisher) {
            val files = this.context.files
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

data class CheckerContext(val jobId: Int, val addRequest: AddRequest, var files: List<SampleFile>? = null)