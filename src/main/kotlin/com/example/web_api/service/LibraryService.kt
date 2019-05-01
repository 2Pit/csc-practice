package com.example.web_api.service

import com.example.web_api.model.Libraries
import com.example.web_api.model.Library
import com.example.web_api.model.Repositories
import org.jetbrains.exposed.sql.ResultRow

object LibraryService : AbstractParenService<Library>(Libraries, Libraries.ownerRepoId, Repositories) {
    override val parentIdKey: String
        get() = "repo_id"

    override fun convert(row: ResultRow): Library {
        return Library(
            id = row[Libraries.id].value,
            ownerRepoId = row[Libraries.ownerRepoId].value,
            path = row[Libraries.path],
            name = row[Libraries.name],
            description = row[Libraries.description],
            tags = row[Libraries.tags]
        )
    }
}