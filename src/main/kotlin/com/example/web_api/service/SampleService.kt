package com.example.web_api.service

import com.example.web_api.model.Sample
import com.example.web_api.model.Samples
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class SampleService {

    fun getSample(id: Int): Sample? {
        return transaction {
            Samples.select { Samples.id eq id }
                .mapNotNull { toSample(it) }
                .singleOrNull()
        }
    }

    fun getAllSamples(): List<Sample> {
        return transaction {
            Samples.selectAll().map { toSample(it) }
        }
    }

    private fun toSample(row: ResultRow): Sample {
        return Sample(
            id = row[Samples.id].value,
            lib = row[Samples.lib],
            name = row[Samples.name],
            buildSystem = row[Samples.buildSystem],
            path = row[Samples.path],
            description = row[Samples.description],
            tags = row[Samples.tags]
        )
    }
}