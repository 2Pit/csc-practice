package com.example.web_api.service

import com.example.web_api.model.Libraries
import com.example.web_api.model.Library
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class LibriaryService {

    fun getLibriary(id: Int): Library? {
        return transaction {
            Libraries.select { Libraries.id eq id }
                .mapNotNull { toLibrary(it) }
                .singleOrNull()
        }
    }

    private fun toLibrary(row: ResultRow): Library {
        return Library(
            id = row[Libraries.id].value,
            ownerRepo = row[Libraries.ownerRepo],
            name = row[Libraries.name],
            description = row[Libraries.description],
            tags = row[Libraries.tags]
        )
    }
}