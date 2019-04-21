package com.example.web_api.service

import com.example.web_api.model.Libraries
import com.example.web_api.model.Library
import org.jetbrains.exposed.sql.ResultRow

class LibraryService : AbstractService<Library>(Libraries) {
    override fun convert(row: ResultRow): Library {
        return Library(
            id = row[Libraries.id].value,
            ownerRepoId = row[Libraries.ownerRepoId].value,
            name = row[Libraries.name],
            description = row[Libraries.description],
            tags = row[Libraries.tags]
        )
    }
}