package com.example.web_api.pipeline

import com.example.web_api.model.AddRequest
import com.example.web_api.service.LibraryService
import com.example.web_api.service.RepositoryService
import io.ktor.util.pipeline.Pipeline
import io.ktor.util.pipeline.PipelinePhase

object Checker {
    val repoChecker = PipelinePhase("RepoChecker")
    val libChecker = PipelinePhase("LibChecker")
    val sampleChecker = PipelinePhase("SampleChecker")
    val checker = Pipeline<Unit, AddRequest>(repoChecker, libChecker, sampleChecker)

    init {
        checker.intercept(repoChecker) {
            val addRequest = this.context
            if (addRequest.repoId != null) {
                RepositoryService.getById(addRequest.repoId) ?: throw RuntimeException("There is no such repo.")
            } else {
                val repository = RepositoryService.getBy(addRequest.repo!!.owner, addRequest.repo.repo)
                if (repository !== null) {
                    throw RuntimeException("Repository already exist.")
                }
            }
        }

        checker.intercept(libChecker) {
            val addRequest = this.context
            if (addRequest.repoId != null && addRequest.libId == null) {
                throw RuntimeException("Old lib with new repo.")
            }
            if (addRequest.libId != null) {
                LibraryService.getById(addRequest.libId) ?: throw RuntimeException("There is no such lib.")
            }
        }
    }
}