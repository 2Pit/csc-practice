package com.example.web_api.pipeline

import com.example.Either
import com.example.git.Property
import com.example.web_api.new_model.Libraries
import com.example.web_api.new_model.Repositories
import com.example.web_api.pipeline.git.Connector
import com.example.web_api.pipeline.git.SampleBuilder
import com.example.web_api.pipeline.git.SampleRequest
import com.example.web_api.service.JobService
import com.example.web_api.service.LibraryService
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
    private val repoChecker = PipelinePhase("RepoChecker")
    private val libChecker = PipelinePhase("LibChecker")
    private val sampleChecker = PipelinePhase("SampleChecker")

    val checker = Pipeline<Unit, CheckerContext>(libChecker, repoChecker, sampleChecker)

    init {
        checker.intercept(libChecker) {
            val chCtx = this.context
            val lib = chCtx.addRequest.lib
            val library = LibraryService.getBy(lib.owner, lib.name)
            if (library == null) {
                when (val res = Connector.getRepository(lib.owner, lib.name)) {
                    is Either.Left -> {
                        JobService.unpdate(chCtx.jobId, "Err", "No such library.")
                        throw res.left
                    }
                    is Either.Right -> transaction {
                        Libraries.insert {
                            it[owner] = lib.owner
                            it[name] = lib.name
                            it[description] = res.right.description
                            it[topics] = "INVALID"
                        }
                    }
                }
            }
        }

        checker.intercept(repoChecker) {
            val chCtx = this.context
            val repo = chCtx.addRequest.repo
            val repository = RepositoryService.getBy(repo.owner, repo.name, repo.branch)

            if (repository == null) {
                when (val res = Connector.getRepository(repo.owner, repo.name)) {
                    is Either.Left -> {
                        JobService.unpdate(chCtx.jobId, "Err", "No such repository.")
                        throw res.left
                    }
                    is Either.Right -> transaction {
                        Repositories.insert {
                            it[owner] = repo.owner
                            it[name] = repo.name
                            it[branch] = repo.branch
                        }
                        JobService.unpdate(chCtx.jobId, "In progress", "The Repository added.")
                    }
                }
            } else {
                JobService.unpdate(chCtx.jobId, "In progress", "The Repository already exist.")
            }
        }

        checker.intercept(sampleChecker) {
            val chCtx = this.context
            val addRequest = chCtx.addRequest
            val repo = addRequest.repo
            val repository = RepositoryService.getBy(repo.owner, repo.name, repo.branch)!!

            JobService.unpdate(chCtx.jobId, "In progress", "Sample checking.")
            addRequest.samples.forEach {
                val sampleRequest = SampleRequest.new(repository.owner, repository.name, repository.branch, it.path)
                val sample = SampleBuilder.buildSample(sampleRequest)
                SampleBuilder.write(sampleRequest, sample)
                notifyJenkins(sampleRequest)
            }
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
    val repo: Repo,
    val lib: Lib,
    val samples: List<SampleNew>
)

data class Repo(val owner: String, val name: String, val branch: String)

data class Lib(val owner: String, val name: String)

data class SampleNew(
    val name: String,
    val buildSystem: String,
    val path: String,
    val description: String,
    val tags: String
)