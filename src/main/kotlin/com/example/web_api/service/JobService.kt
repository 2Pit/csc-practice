package com.example.web_api.service

import com.example.web_api.model.Job
import com.example.web_api.model.Jobs
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object JobService : AbstractService<Job>(Jobs) {
    override fun convert(row: ResultRow): Job {
        return Job(
            id = row[Jobs.id].value,
            context = row[Jobs.context],
            status = row[Jobs.status],
            message = row[Jobs.message]
        )
    }

    fun create(cntx: String): Int {
        val entityID = transaction {
            Jobs.insert {
                it[context] = cntx
                it[status] = "new"
                it[message] = ""
            } get Jobs.id
        }
        return entityID!!.value
    }
}