package com.example.app.db

/**
 *
 *  Location <--+ Sample <--+ Snapshot <--+ File
 *
 *  Job
 */

interface DataRow {
    val id: Int
}

interface DataFilter {
    val id: Int?
}

data class RepositoryRow(
    override val id: Int,
    val owner: String,
    val repo: String,
    val branch: String
) : DataRow

data class RepositoryFilter(
    override val id: Int? = null,
    val owner: String? = null,
    val repo: String? = null,
    val branch: String? = null
) : DataFilter

data class SampleRow(
    override val id: Int,
    val repositoryId: Int,
    val validSnapshotId: Int?,
    val name: String,
    val path: String
) : DataRow

data class SampleFilter(
    override val id: Int? = null,
    val repositoryId: Int? = null,
    val validSnapshotId: Int? = null,
    val name: String? = null,
    val path: String? = null
) : DataFilter

data class SnapshotRow(
    override val id: Int,
    val sampleId: Int,
    val sha: String,
    val status: String,
    val buildSystem: String,
    val readme: String
//    val archiveLink : String,
) : DataRow

data class SnapshotFilter(
    override val id: Int? = null,
    val sampleId: Int? = null,
    val sha: String? = null,
    val status: String? = null,
    val buildSystem: String? = null,
    val readme: String? = null
) : DataFilter

data class FileRow(
    override val id: Int,
    val snapshotId: Int,
    val content: ByteArray
) : DataRow

data class FileFilter(
    override val id: Int? = null,
    val snapshotId: Int? = null,
    val content: ByteArray? = null
) : DataFilter


data class Job(
    val id: Int,
    val status: String,
    val description: String,
    val context: String
)

