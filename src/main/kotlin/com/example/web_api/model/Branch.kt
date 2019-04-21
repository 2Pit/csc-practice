package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable


object Branches : IntIdTable() {
    val repoId = reference("repo_id", Repositories)
    val name = varchar("name", 50)
    val sha = varchar("sha", 50)
}

data class Branch(
    val id: Int,
    val repoId: Int,
    val name: String,
    val sha: String
)
