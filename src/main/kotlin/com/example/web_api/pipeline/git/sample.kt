package com.example.web_api.pipeline.git

import com.example.git.Property
import com.example.git.base64toUtf8
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
        val repository = RepositoryId.create(request.owner, request.name)
        val firstLevel = contentService.getContents(repository, request.path)
            .groupBy { it.type }

        firstLevel.values
            .flatten()
            .forEach { it.path = it.path.removePrefix(request.path) }

        val files = mutableListOf<SampleFile>()
        firstLevel[TYPE_FILE]?.mapTo(files) { downloadFile(repository, it) }
        firstLevel[TYPE_DIR]?.forEach { rc ->
            getBlobs(repository, rc.sha).mapTo(files) { treeEntry ->
                downloadFile(repository, treeEntry, rc.path)
            }
        }

        return Sample(files)
    }

    fun write(request: SampleRequest, sample: Sample) {
        sample.files.forEach { sf ->
            val file = File(
                "/home/petr/Documents/Jenkins/repo_sample/${request.getPathAtLocatRepo()}",
                sf.path
            )
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            file.printWriter().use { out -> out.print(sf.content) }
//            if (file.name == "gradlew") { // TODO fix gradle running
            file.setExecutable(true)
//            }
        }
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