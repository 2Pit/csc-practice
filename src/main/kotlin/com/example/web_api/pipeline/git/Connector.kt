package com.example.web_api.pipeline.git

import com.example.Either
import com.example.Failure
import com.example.Result
import com.example.Success
import com.example.git.Property
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.RepositoryContents
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.ContentsService
import org.eclipse.egit.github.core.service.RepositoryService
import java.io.IOException

typealias Result<V> = Either<Exception, V>

inline fun <V> (Result<V>).raise(block: () -> Unit) =
    when (this) {
        is Either.Left -> {
            block()
            throw this.left
        }
        is Either.Right -> this.right
    }

object Connector {

    private val client = GitHubClient().apply { setOAuth2Token(Property.githubToken) }
    private val repositoryService = RepositoryService(client)
    private val contentService = ContentsService(client)


    fun getRepository(owner: String, name: String): Result<Repository> {
        return try {
            Success(repositoryService.getRepository(owner, name))
        } catch (e: IOException) {
            Failure(e)
        }
    }

    fun getContent(repository: Repository, path: String): Result<MutableList<RepositoryContents>> {
        return try {
            Success(contentService.getContents(repository, path))
        } catch (e: IOException) {
            Failure(e)
        }

    }
}