package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable

object Repositories : IntIdTable() {
    val owner = varchar("name", 50)
    val repo = varchar("repo", 50)
//    val branches by Branch referrersOn Branch.repo
}

data class Repository(
    val owner: String,
    val repo: String
)