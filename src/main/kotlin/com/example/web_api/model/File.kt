package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable


object Files : IntIdTable() {
    val path = varchar("path", 50)
    val name = varchar("name", 50)
    val extension = varchar("extension", 50)
    val content = text("content")
}

data class File(
    var path: String,
    var name: String,
    var extension: String,
    var content: String
)