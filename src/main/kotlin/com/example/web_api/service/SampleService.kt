package com.example.web_api.service

import com.example.web_api.model.Libraries
import com.example.web_api.model.Sample
import com.example.web_api.model.Samples
import org.jetbrains.exposed.sql.ResultRow

object SampleService : AbstractParenService<Sample>(Samples, Samples.libId, Libraries) {
    override val parentIdKey: String
        get() = "lib_id"

    override fun convert(row: ResultRow): Sample {
        return Sample(
            id = row[Samples.id].value,
            libId = row[Samples.libId].value,
            name = row[Samples.name],
            buildSystem = row[Samples.buildSystem],
            path = row[Samples.path],
            description = row[Samples.description],
            tags = row[Samples.tags]
        )
    }
}