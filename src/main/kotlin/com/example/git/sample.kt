package com.example.git

import org.eclipse.egit.github.core.RepositoryContents
import org.eclipse.egit.github.core.RepositoryContents.*
import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.TreeEntry
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.ContentsService
import org.eclipse.egit.github.core.service.DataService

/**
 * The Sample Request contains into to build a Sample.
 *
 * @property owner repository owner
 * @property repo repository name
 * @property path path to sample
 */
data class SampleRequest(
    val owner: String,
    val repo: String,
    val path: String
) {
    init {
        assert(path.endsWith("/"))
    }
}

/**
 * The Sample class.
 * Contains metadata and list of files.
 *
 * @property files list of files
 */
class Sample(
    val files: List<SampleFile>
//    val meta
)

data class SampleFile(
    val path: String,
//    val name: String,
    val content: String
)

object SampleBuilder {
    private val client = GitHubClient().apply { setOAuth2Token(Property.githubToken) }
    private val contentService = ContentsService(client)
    private val dataService = DataService(client)

    fun buildSample(request: SampleRequest): Sample {
        val repository = RepositoryId.create(request.owner, request.repo)
        val firstLevel = contentService.getContents(repository, request.path)
            .groupBy { it.type }

        firstLevel.values
            .flatten()
            .forEach { it.path = it.path.removePrefix(request.path) }

        val files = mutableListOf<SampleFile>()
        firstLevel[TYPE_FILE]?.mapTo(files) { downloadFile(repository, it) }
        (firstLevel[TYPE_DIR] ?: emptyList())
            .forEach { rc ->
                getBlobs(repository, rc.sha).mapTo(files) { treeEntry ->
                    downloadFile(repository, treeEntry, rc.path)
                }
            }

        return Sample(files)
    }

    private fun getBlobs(repository: RepositoryId, sha: String): List<TreeEntry> {
        val tree = dataService.getTree(repository, sha, true)
//        if (tree.truncated) TODO: fix it
        return tree.tree.filter { it.type == "blob" }
    }

    private fun downloadFile(repository: RepositoryId, content: RepositoryContents): SampleFile {
        val blob = dataService.getBlob(repository, content.sha)
        return SampleFile(
            content.path,
            blob.content.base64toUtf8()
        )
    }

    private fun downloadFile(repository: RepositoryId, treeEntry: TreeEntry, pathPrefix: String): SampleFile {
        val blob = dataService.getBlob(repository, treeEntry.sha)
        return SampleFile(
            pathPrefix + "/" + treeEntry.path,
            blob.content.base64toUtf8()
        )
    }
}