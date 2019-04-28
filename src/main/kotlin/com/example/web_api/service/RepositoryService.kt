package com.example.web_api.service

import com.example.web_api.model.Repositories
import com.example.web_api.model.Repository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object RepositoryService : AbstractService<Repository>(Repositories) {
    override fun convert(row: ResultRow): Repository {
        return Repository(
            id = row[Repositories.id].value,
            owner = row[Repositories.owner],
            repo = row[Repositories.repo]
        )
    }

    fun getBy(owner: String, repo: String): Repository? {
        var ans: Repository? = null
        transaction {
            ans = Repositories.select { Repositories.owner eq owner }
                .andWhere { Repositories.repo eq repo }
                .map { convert(it) }
                .firstOrNull()
        }
        return ans
    }
}