package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable

object Snapshots : IntIdTable() {
    val repo = varchar("repo", 50)
    val branch = varchar("branch", 50)
}

data class Snapshot(
    val repo: String,
    val branch: String
)