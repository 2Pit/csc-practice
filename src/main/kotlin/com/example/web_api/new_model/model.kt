package com.example.web_api.new_model

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

object Locations : IntIdTable(), Service<LocationRow, LocationFilter> {
    override fun getBy(filter: LocationFilter): List<LocationRow> {
        TODO("not implemented")
    }

    override fun getBy(id: Int): LocationRow? {
        TODO("not implemented")
    }

    val owner = varchar("owner", 50)
    val name = varchar("name", 50)
    val branch = varchar("branch", 50)
    val path = varchar("path", 100)

    override fun convert(row: ResultRow): LocationRow {
        return LocationRow(
            id = row[id].value,
            owner = row[owner],
            name = row[name],
            branch = row[branch],
            path = row[path]
        )
    }
}

object Samples : IntIdTable(), Service<SampleRow, SampleFilter> {
    val locationId = reference("location_id", Locations)
    val name = varchar("name", 50)
    val buildSystem = varchar("buildSystem", 50) // TODO to enum

    override fun getBy(id: Int): SampleRow? {
        TODO("not implemented")
    }

    override fun getBy(filter: SampleFilter): List<SampleRow> {
        TODO("not implemented")
    }

    override fun convert(row: ResultRow): SampleRow {
        return SampleRow(
            id = row[id].value,
            locationId = row[locationId].value,
            name = row[name],
            buildSystem = row[buildSystem]
        )
    }
}

object Snapshots : IntIdTable(), Service<SnapshotRow, SampleFilter> {
    val sampleId = reference("sample_id", Samples)
    val sha = varchar("sha", 50)
    val status = varchar("status", 50)
    //    val archiveLink = text("archiveLink")

    override fun getBy(id: Int): SnapshotRow? {
        TODO("not implemented")
    }

    override fun getBy(filter: SampleFilter): List<SnapshotRow> {
        TODO("not implemented")
    }

    override fun convert(row: ResultRow): SnapshotRow {
        return SnapshotRow(
            id = row[id].value,
            sampleId = row[sampleId].value,
            sha = row[sha],
            status = row[status]
        )
    }
}

object Files : IntIdTable(), Service<FileRow, FileFilter> {
    val snapshotId = reference("snapshot_id", Snapshots)
    val path = varchar("path", 50)
    val name = varchar("name", 50)
    val extension = varchar("extension", 50)
    val content = text("content")
    val type = varchar("type", 50)

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
            path = row[path],
            name = row[name],
            extension = row[extension],
            content = row[content],
            type = row[type]
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
