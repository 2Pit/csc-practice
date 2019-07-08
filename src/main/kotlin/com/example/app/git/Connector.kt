package com.example.app.git

import arrow.core.Try
import arrow.core.extensions.`try`.monadThrow.bindingCatch
import com.example.app.api.Location
import com.example.git.Property
import com.example.git.base64toUtf8
import org.eclipse.egit.github.core.*
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.CommitService
import org.eclipse.egit.github.core.service.ContentsService
import org.eclipse.egit.github.core.service.DataService
import org.eclipse.egit.github.core.service.RepositoryService
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import com.example.app.db.Repository as Repo


interface GitInteraction {
    fun getLatestCommitSha(repo: Repo, path: String): Try<String>
    fun getDirSha(repo: Repo, commitSha: String, path: String = ""): Try<String>
    fun downloadSample(repo: Repo, dirSha: String): Try<List<SampleFile>>
}

object Connector : GitInteraction {
    private val client = GitHubClient().apply { setOAuth2Token(Property.githubToken) }
    val repositoryService = RepositoryService(client)
    val commitService = CommitService(client)
    val contentService = ContentsService(client)
    val dataService = DataService(client)

    fun getRepository(owner: String, name: String): Try<Repository> {
        return Try { repositoryService.getRepository(owner, name) }
    }

    fun getContent(repository: Repository, path: String): Try<MutableList<RepositoryContents>> {
        return Try { contentService.getContents(repository, path) }
    }

    override fun getDirSha(repo: Repo, commitSha: String, path: String): Try<String> =
        bindingCatch {
            if (path.isEmpty()) {
                commitSha
            } else {
                val (parent, child) = File(path).run { parent to name }
                val repository = repositoryService.getRepository(repo.owner, repo.name)
                contentService.getContents(repository, parent, commitSha)
                    .first { it.name == child }
                    .sha
            }
        }


    override fun getLatestCommitSha(repo: Repo, path: String): Try<String> {
        return Try {
            val cmts = Connector.commitService.getCommits(repo.toRepositoryId())
            cmts.sortByDescending { it.commit.author.date }
            cmts.first().sha
        }
    }

    override fun downloadSample(repo: Repo, dirSha: String): Try<List<SampleFile>> {
        return Try {
            val repositoryId = RepositoryId.create(repo.owner, repo.name)
            getBlobs(repositoryId, dirSha)
                .map { treeEntry ->
                    SampleFile(
                        treeEntry.path,
                        dataService.getBlob(repositoryId, treeEntry.sha).content.base64toUtf8()
                    )
                }
        }
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

fun List<SampleFile>.compress(): ByteArrayOutputStream {
    val zipResult = ByteArrayOutputStream()

    val zipOutputStream = ZipOutputStream(BufferedOutputStream(zipResult))
    zipOutputStream.use { zos ->
        this.forEach { file ->
            zos.putNextEntry(ZipEntry(file.path))
            zos.write(file.content.toByteArray())
            zos.closeEntry()
        }
    }
    return zipResult
}

fun ByteArrayOutputStream.write(file: File) {
    file.parentFile.mkdirs()
    this.writeTo(file.outputStream())
}


fun write(path: String, location: Location, sample: List<SampleFile>) {
    sample.forEach { sf ->
        val file = File(
            path + location.getPathAtLocalRepo(),
            sf.path
        )
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.printWriter().use { out -> out.print(sf.content) }
    }
}