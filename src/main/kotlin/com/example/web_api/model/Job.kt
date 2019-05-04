package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable

object Jobs : IntIdTable() {
    val context = varchar("context", 500)
    val status = varchar("status", 50)
    val message = varchar("message", 50)
}

data class Job(
    val id: Int,
    val context: String,
    val status: String,
    val message: String
)