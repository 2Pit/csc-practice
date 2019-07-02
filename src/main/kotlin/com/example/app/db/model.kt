package com.example.app.db

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

/**
 *
 *  Location <--+ Sample <--+ Snapshot <--+ File
 *
 *  Job
 */
interface Service<R : DataRow, F : DataFilter> {
    fun convert(row: ResultRow): R

    fun getBy(filter: F): List<R>

    fun getBy(id: Int): R?
}

object Repositories : IntIdTable(), Service<RepositoryRow, RepositoryFilter> {
    val owner = varchar("owner", 50)
    val repo = varchar("name", 50)
    val branch = varchar("branch", 50)

    override fun getBy(filter: RepositoryFilter): List<RepositoryRow> {
        TODO("not implemented")
    }

    override fun getBy(id: Int): RepositoryRow? {
        TODO("not implemented")
    }

    override fun convert(row: ResultRow): RepositoryRow {
        return RepositoryRow(
            id = row[id].value,
            owner = row[owner],
            repo = row[repo],
            branch = row[branch]
        )
    }
}

object Samples : IntIdTable(), Service<SampleRow, SampleFilter> {
    val repositoryId = reference("repository_id", Repositories)
    val path = varchar("path", 100)
    val name = varchar("name", 50)
    val validSnapshotId = reference("valid_snapshot_id",Snapshots).nullable()
//    val buildSystem = varchar("buildSystem", 50) // TODO to enum

    override fun getBy(id: Int): SampleRow? {
        TODO("not implemented")
    }

    override fun getBy(filter: SampleFilter): List<SampleRow> {
        TODO("not implemented")
    }

    override fun convert(row: ResultRow): SampleRow {
        return SampleRow(
            id = row[id].value,
            repositoryId = row[repositoryId].value,
            validSnapshotId = row[validSnapshotId]?.value,
            name = row[name],
            path = row[path]
        )
    }
}

object Snapshots : IntIdTable(), Service<SnapshotRow, SnapshotFilter> {
    val sampleId = reference("sample_id", Samples)
    val sha = varchar("sha", 50)
    val status = varchar("status", 50)
    val buildSystem = varchar("buildSystem", 50) // TODO to enum
    val readme = varchar("readme", 50) // TODO to enum
//    val zipUrl
    //    val archiveLink = text("archiveLink")

    override fun getBy(id: Int): SnapshotRow? {
        TODO("not implemented")
    }

    override fun getBy(filter: SnapshotFilter): List<SnapshotRow> {
        TODO("not implemented")
    }

    override fun convert(row: ResultRow): SnapshotRow {
        return SnapshotRow(
            id = row[id].value,
            sampleId = row[sampleId].value,
            sha = row[sha],
            status = row[status],
            buildSystem = row[buildSystem],
            readme = row[readme]
        )
    }
}

object Files : IntIdTable(), Service<FileRow, FileFilter> {
    val snapshotId = reference("snapshot_id", Snapshots).primaryKey()
    val content = text("content")

    override fun getBy(filter: FileFilter): List<FileRow> {
        TODO("not implemented")
    }

    override fun getBy(id: Int): FileRow? {
        TODO("not implemented")
    }

    override fun convert(row: ResultRow): FileRow {
        return FileRow(
            id = row[id].value,
            snapshotId = row[snapshotId].value,
            content = row[content].toByteArray()
        )
    }
}

object Jobs : IntIdTable() {
    val status = varchar("status", 50) // new, in_progress, done, error
    val description = text("description")
    val context = text("context") // context in json format

    fun convert(row: ResultRow): Job {
        return Job(
            id = row[id].value,
            status = row[status],
            description = row[description],
            context = row[context]
        )
    }
}
