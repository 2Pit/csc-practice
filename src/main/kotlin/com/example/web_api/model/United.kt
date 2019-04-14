package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable


object Branches : IntIdTable() {
    val repo = varchar("repo", 50)
    val name = varchar("name", 50)
    val sha = varchar("sha", 50)
}

data class Branch(
    val repo: String,
    val name: String,
    val sha: String
)




