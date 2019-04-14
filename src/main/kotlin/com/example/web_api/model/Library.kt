package com.example.web_api.model

import org.jetbrains.exposed.dao.IntIdTable


object Libraries : IntIdTable() {
    val ownerRepo = varchar("ownerRepo", 50)
    val name = varchar("name", 50)
    val description = varchar("description", 50)
    val tags = text("tags")
}

data class Library(
    val id: Int,
    val ownerRepo: String,
    val name: String,
    val description: String,
    val tags: String
)