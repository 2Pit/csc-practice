package com.example.web_api.new_model

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

/**
 *  Library    <------+
 *                    +
 *  Repository <--+ Sample <--+ Snapshot <--+ File
 *
 *  Job
 */
object Repositories : IntIdTable() {
    val owner = varchar("owner", 50)
    val name = varchar("name", 50) // aka name
    val branch = varchar("branch", 50)

    fun convert(row: ResultRow): Repository {
        return Repository(
            id = row[id].value,
            owner = row[owner],
            name = row[name],
            branch = row[branch]
        )
    }
}

object Libraries : IntIdTable() {
    val owner = varchar("owner", 50)
    val name = varchar("name", 50) // aka name
    val description = text("description")
    val topics = text("topics") // TODO to list

    fun convert(row: ResultRow): Library {
        return Library(
            id = row[id].value,
            owner = row[owner],
            name = row[name],
            description = row[description],
            topics = row[topics]
        )
    }
}

object Samples : IntIdTable() {
    val repoId = reference("repo_id", Repositories)
    val libId = reference("lib_id", Libraries)
    //    val validSnapshotId = reference("valid_snapshot_id", Snapshots).nullable() // TODO discuss
    val name = varchar("name", 50)

    val buildSystem = varchar("buildSystem", 50) // TODO to enum
    val path = varchar("path", 100)
    val description = text("description")
    val topics = text("topics") // TODO to list

    fun convert(row: ResultRow): Sample {
        return Sample(
            id = row[id].value,
            repoId = row[repoId].value,
            libId = row[libId].value,
            name = row[name],
            buildSystem = row[buildSystem],
            path = row[path],
            description = row[description],
            topics = row[topics]
        )
    }
}

object Snapshots : IntIdTable() {
    val sampleId = reference("sample_id", Samples)
    val sha = varchar("sha", 50)
    val status = varchar("status", 50)
//    val archiveLink = text("archiveLink")

    fun convert(row: ResultRow): Snapshot {
        return Snapshot(
            id = row[id].value,
            sampleId = row[sampleId].value,
            sha = row[sha],
            status = row[status]
        )
    }
}

object Files : IntIdTable() {
    val snapshotId = reference("snapshot_id", Snapshots)
    val path = varchar("path", 50)
    val name = varchar("name", 50)
    val extension = varchar("extension", 50)
    val content = text("content")

    fun convert(row: ResultRow): File {
        return File(
            id = row[id].value,
            snapshotId = row[snapshotId].value,
            path = row[path],
            name = row[name],
            extension = row[extension],
            content = row[content]
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
