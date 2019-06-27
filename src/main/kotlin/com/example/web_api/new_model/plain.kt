package com.example.web_api.new_model

/**
 *
 *  Location <--+ Sample <--+ Snapshot <--+ File
 *
 *  Job
 */

/**
 * Marker interface for non-null data classes.
 */
interface DataRow

/**
 * Marker interface for nullable data classes for searching in DB.
 */
interface DataFilter

data class LocationRow(
    val id: Int,
    val owner: String,
    val name: String, // TODO split repository?
    val branch: String,
    val path: String
) : DataRow

data class SampleRow(
    val id: Int,
    val locationId: Int,
    val name: String,
    val buildSystem: String
): DataRow

data class SnapshotRow(
    val id: Int,
    val sampleId: Int,
    val sha: String,
    val status: String
//    val archiveLink : String,
): DataRow

data class FileRow(
    val id: Int,
    val snapshotId: Int,
    val path: String,
    val name: String,
    val extension: String,
    val type: String,
    val content: String
): DataRow


data class LocationFilter(
    val id: Int? = null,
    val owner: String? = null,
    val name: String? = null,
    val branch: String? = null,
    val path: String? = null
) : DataFilter

data class SampleFilter(
    val id: Int? = null,
    val locationId: Int? = null,
    val name: String? = null,
    val buildSystem: String? = null
) : DataFilter

data class SnapshotFilter(
    val id: Int? = null,
    val sampleId: Int? = null,
    val sha: String? = null,
    val status: String? = null
) : DataFilter

data class FileFilter(
    val id: Int? = null,
    val snapshotId: Int? = null,
    val path: String? = null,
    val name: String? = null,
    val extension: String? = null,
    val type: String? = null,
    val content: String? = null
) : DataFilter


data class Job(
    val id: Int,
    val status: String,
    val description: String,
    val context: String
)