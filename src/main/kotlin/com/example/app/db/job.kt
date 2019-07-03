package com.example.app.db

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

object Jobs : AbstractService<JobRow, JobFilter>() {
    val jobId = integer("job_id").autoIncrement().index()
    val status = varchar("status", 50) // new, in_progress, done, error
    val description = text("description")
    val context = text("context") // context in json format

    fun insert(item: JobRow): Int {
        return transaction {
            Jobs.insertAndGetId {
                if (item.jobId > 0) it[jobId] = item.jobId
                it[status] = item.status
                it[description] = item.description
                it[context] = item.context
            }
        }.value
    }

    override fun buildFilterQuery(filter: JobFilter): Query {
        TODO("not implemented")
    }

    override fun convert(row: ResultRow): JobRow {
        return JobRow(
            id = row[id].value,
            jobId = row[jobId],
            status = row[status],
            description = row[description],
            context = row[context]
        )
    }
}

data class JobRow(
    val id: Int,
    val jobId: Int,
    val status: String,
    val description: String,
    val context: String
) : DataRow

data class JobFilter(
    val id: Int? = null,
    val jobId: Int? = null,
    val status: String? = null,
    val description: String? = null,
    val context: String? = null
) : DataFilter