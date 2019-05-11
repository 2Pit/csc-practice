package com.example.web_api.new_model

/**
 *  Library    <------+
 *                    +
 *  Repository <--+ Sample <--+ Snapshot <--+ File
 *
 *  Job
 */

data class Repository(
    val id: Int,
    val owner: String,
    val name: String,
    val branch: String
)

data class Library(
    val id: Int,
    val owner: String,
    val name: String,
    val description: String,
    val topics: String
)

data class Sample(
    val id: Int,
    val repoId: Int,
    val libId: Int,
    val name: String,
    val buildSystem: String,
    val path: String,
    val description: String,
    val topics: String
)

data class Snapshot(
    val id: Int,
    val sampleId: Int,
    val sha: String,
    val status: String
//    val archiveLink : String,
)

data class File(
    val id: Int,
    val snapshotId: Int,
    val path: String,
    val name: String,
    val extension: String,
    val content: String
)


data class Job(
    val id: Int,
    val status: String,
    val description: String,
    val context: String
)