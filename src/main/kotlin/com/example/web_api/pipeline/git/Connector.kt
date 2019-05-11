package com.example.web_api.pipeline.git

import com.example.Either
import com.example.Failure
import com.example.Success
import com.example.git.Property
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.RepositoryService
import java.io.IOException

typealias Result<V> = Either<Exception, V>

object Connector {

    private val client = GitHubClient().apply { setOAuth2Token(Property.githubToken) }
    private val repositoryService = RepositoryService(client)


    fun getRepository(owner: String, name: String): Result<Repository> {
        return try {
            Success(repositoryService.getRepository(owner, name))
        } catch (e: IOException) {
            Failure(e)
        }
    }
}