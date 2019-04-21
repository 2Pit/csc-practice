package com.example.web_api.service

import com.example.web_api.model.File
import com.example.web_api.model.Files
import org.jetbrains.exposed.sql.ResultRow

class FileService : AbstractService<File>(Files) {

    override fun convert(row: ResultRow): File {
        return File(
            branchId = row[Files.branchId].value,
            path = row[Files.path],
            name = row[Files.name],
            extension = row[Files.extension],
            content = row[Files.content]
        )
    }
}