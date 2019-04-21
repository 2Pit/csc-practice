package com.example.web_api.service

import com.example.web_api.model.Repositories
import com.example.web_api.model.Repository
import org.jetbrains.exposed.sql.ResultRow

class RepositoryService : AbstractService<Repository>(Repositories) {
    override fun convert(row: ResultRow): Repository {
        return Repository(
            owner = row[Repositories.owner],
            repo = row[Repositories.repo]
        )
    }
}