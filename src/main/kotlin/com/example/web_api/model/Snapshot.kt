package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable

object Snapshots : IntIdTable() {
    val repoId = reference("repo_id", Repositories)
    val branch = varchar("branch", 50)
}

data class Snapshot(
    val repoId: Int,
    val branch: String
)