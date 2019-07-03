package com.example.app

import arrow.core.extensions.`try`.monad.binding
import arrow.core.getOrElse
import com.example.app.api.AddRequest
import com.example.app.db.JobRow
import com.example.app.db.Jobs
import com.example.app.db.Repositories
import com.example.app.db.RepositoryFilter
import com.example.app.git.*
import com.example.git.Property
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.util.pipeline.Pipeline
import io.ktor.util.pipeline.PipelinePhase
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.transaction
import java.io.File

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
                Jobs.insert(JobRow(-1, chCtx.jobId, "In progress", "he Location already exist.", ""))
                return@intercept
            }

            binding {
                val (repository) = Connector.getRepository(location.owner, location.repo)
                val (content) = Connector.getContent(repository, location.path)
            }.getOrElse { ex ->
                Jobs.insert(JobRow(-1, chCtx.jobId, "Err", "No such location.", ""))
                throw ex
            }

            transaction {
                Repositories.insert {
                    it[owner] = location.owner
                    it[repo] = location.repo
                    it[branch] = location.branch
                }
            }

            Jobs.insert(JobRow(-1, chCtx.jobId, "In progress", "The Repository added.", ""))
        }

        checker.intercept(sampleChecker) {
            val chCtx = this.context
            val addRequest = chCtx.addRequest
            val location = addRequest.location

            Jobs.insert(JobRow(-1, chCtx.jobId, "In progress", "Sample checking.", ""))
            val files = Connector.downloadProjectFiles(location)
//            val zipSampleOutputStream = compress(files)


//                TODO: Turn on Jenkins
//                SampleBuilder.write(sampleRequest, sample)
//                notifyJenkins(sampleRequest)
            this.context.files = files
        }

        checker.intercept(samplePublisher) {
            val ctx = this.context
            val files = ctx.files!!
            val zipFile = File("/home/peter.bogdanov/IdeaProjects/csc-practice/out/${this.context.addRequest.location.getPathAtLocalRepo()}", "test.zip")
            files.compress().write(zipFile)

            Jobs.insert(JobRow(-1, ctx.jobId, "Done", "Zip file created.", ""))
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