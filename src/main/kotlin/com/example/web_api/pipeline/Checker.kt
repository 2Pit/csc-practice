package com.example.web_api.pipeline

import com.example.web_api.model.AddRequest
import com.example.web_api.pipeline.git.SampleBuilder
import com.example.web_api.pipeline.git.SampleRequest
import com.example.web_api.service.LibraryService
import com.example.web_api.service.RepositoryService
import io.ktor.util.pipeline.Pipeline
import io.ktor.util.pipeline.PipelinePhase

object Checker {
    private val repoChecker = PipelinePhase("RepoChecker")
    private val libChecker = PipelinePhase("LibChecker")
    private val sampleChecker = PipelinePhase("SampleChecker")

    val checker = Pipeline<Unit, AddRequest>(repoChecker, libChecker, sampleChecker)

    init {
        checker.intercept(repoChecker) {
            val addRequest = this.context
            if (addRequest.repoId != null) {
                RepositoryService.getById(addRequest.repoId) ?: throw RuntimeException("There is no such repo.")
            } else {
                val repository = RepositoryService.getBy(addRequest.repo!!.owner, addRequest.repo.repo)
                if (repository != null) {
                    throw RuntimeException("Repository already exist.")
                }
            }
        }

        checker.intercept(libChecker) {
            val addRequest = this.context
            if (addRequest.libId != null) {
                LibraryService.getById(addRequest.libId) ?: throw RuntimeException("There is no such lib.")
//            } else {
//                LibraryService.getBy(addRequest.lib!!.)
            }
        }

        checker.intercept(sampleChecker) {
            val addRequest = this.context
            val repository = if (addRequest.repoId != null) {
                RepositoryService.getById(addRequest.repoId)!!
            } else {
                RepositoryService.getBy(addRequest.repo!!.owner, addRequest.repo.repo)!!
            }

            addRequest.samples.forEach {
                val sampleRequest = SampleRequest(repository.owner, repository.repo, it.path)
                val sample = SampleBuilder.buildSample(sampleRequest)
                SampleBuilder.write(sampleRequest, sample)
            }
        }
    }
}