package com.example.app.git

import com.example.app.Properties
import com.example.git.base64toUtf8
import com.example.app.api.Location
import org.eclipse.egit.github.core.RepositoryContents
import org.eclipse.egit.github.core.RepositoryContents.TYPE_DIR
import org.eclipse.egit.github.core.RepositoryContents.TYPE_FILE
import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.TreeEntry
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.ContentsService
import org.eclipse.egit.github.core.service.DataService
import java.io.File

/**
 * The Sample Request contains into to build a Sample.
 *
 * @property owner repository owner
 * @property name repository name
 * @property path path to sample
 */
data class SampleRequest(
    val owner: String,
    val name: String,
    val branch: String,
    val path: String
) {
    companion object {
        fun new(owner: String, name: String, branch: String, path: String): SampleRequest {
            return if (path.endsWith("/")) {
                SampleRequest(owner, name, branch, path)
            } else {
                SampleRequest(owner, name, branch, "$path/")
            }
        }
    }

    init {
        assert(path.endsWith("/"))
    }

    fun getPathAtLocatRepo(): String {
        return "$owner/$name/$branch/"
    }
}


data class SampleFile(
    val path: String,
//    val name: String,
    val content: String
)

object SampleBuilder {
    private val client = GitHubClient().apply { setOAuth2Token(Properties.githubToken) }
    private val contentService = ContentsService(client)
    private val dataService = DataService(client)

    fun downloadProjectFiles(location: Location): List<SampleFile> {
        val repository = RepositoryId.create(location.owner, location.repo)
        val firstLevel = contentService.getContents(repository, location.path)
            .groupBy { it.type }

        firstLevel.values
            .flatten()
            .forEach { it.path = it.path.removePrefix(location.path) }

        val files = mutableListOf<SampleFile>()
        firstLevel[TYPE_FILE]?.mapTo(files) { downloadFile(repository, it) }
        firstLevel[TYPE_DIR]?.forEach { repositoryContent ->
            getBlobs(repository, repositoryContent.sha).mapTo(files) { treeEntry ->
                downloadFile(repository, treeEntry, repositoryContent.path)
            }
        }

        return files
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