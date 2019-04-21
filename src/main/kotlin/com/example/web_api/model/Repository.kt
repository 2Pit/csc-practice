package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

object Repositories : IntIdTable() {
    val owner = varchar("name", 50)
    val repo = varchar("repo", 50)
//    val branches by Branch referrersOn Branch.repo
}

data class Repository(
    val id: Int,
    val owner: String,
    val repo: String
)

object RepositoriesSamples : Table() {
    val repoId = reference("repo_id", Repositories).primaryKey(0)
    val sampleId = reference("sample_id", Samples).primaryKey(1)
}