package com.example.app.api

data class AddRequest(
    val location: Location,
    val name: String
)

data class Location(val owner: String, val repo: String, val path: String, val branch: String = "master") {
    fun getPathAtLocalRepo(): String {
        return "/$owner/$repo/$branch/"
    }
}

data class Repository(
    val owner: String,
    val name: String,
    val branch: String
)

data class SampleInfo(
    val name: String,
    val readmeMD: String,
    val archiveId: Int,
    val path: String
// TODO add libs, tags
)