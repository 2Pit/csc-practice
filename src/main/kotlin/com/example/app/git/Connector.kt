package com.example.app.git

import arrow.core.Try
import com.example.app.api.Location
import com.example.git.Property
import com.example.git.base64toUtf8
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.RepositoryContents
import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.TreeEntry
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.ContentsService
import org.eclipse.egit.github.core.service.DataService
import org.eclipse.egit.github.core.service.RepositoryService
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


object Connector {
    private val client = GitHubClient().apply { setOAuth2Token(Property.githubToken) }
    private val repositoryService = RepositoryService(client)
    private val contentService = ContentsService(client)
    private val dataService = DataService(client)

    fun getRepository(owner: String, name: String): Try<Repository> {
        return Try { repositoryService.getRepository(owner, name) }
    }

    fun getContent(repository: Repository, path: String): Try<MutableList<RepositoryContents>> {
        return Try { contentService.getContents(repository, path) }
    }

    fun downloadProjectFiles(location: Location): List<SampleFile> {
        val repository = RepositoryId.create(location.owner, location.repo)
        val firstLevel = contentService.getContents(repository, location.path)
            .groupBy { it.type }

        firstLevel.values
            .flatten()
            .forEach { it.path = it.path.removePrefix(location.path) }

        val files = mutableListOf<SampleFile>()
        firstLevel[RepositoryContents.TYPE_FILE]?.mapTo(files) { downloadFile(repository, it) }
        firstLevel[RepositoryContents.TYPE_DIR]?.forEach { repositoryContent ->
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

fun compress(files: List<SampleFile>): ByteArrayOutputStream {
    val zipResult = ByteArrayOutputStream()

    val zipOutputStream = ZipOutputStream(BufferedOutputStream(zipResult))
    zipOutputStream.use { zos ->
        files.forEach { file ->
            zos.putNextEntry(ZipEntry(file.path))
            zos.write(file.content.toByteArray())
            zos.closeEntry()
        }
    }
    return zipResult
}

fun write(path: String, location: Location, sample: Sample) {
    sample.files.forEach { sf ->
        val file = File(
            path + location.getPathAtLocatRepo(),
            sf.path
        )
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.printWriter().use { out -> out.print(sf.content) }
    }
}