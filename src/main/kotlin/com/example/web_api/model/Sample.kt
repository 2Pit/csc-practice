package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable

object Samples : IntIdTable() {
    val libId = reference("lib_id", Libraries)
    val name = varchar("name", 50)
    val buildSystem = varchar("buildSystem", 50)
    val path = varchar("path", 50)
    val description = varchar("description", 50)
    val tags = text("tags")
}

data class Sample(
    val id: Int,
    val libId: Int,
    val name: String,
    val buildSystem: String,
    val path: String,
    val description: String,
    val tags: String
)