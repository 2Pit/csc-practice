package com.example.web_api.service

import com.example.web_api.model.Branches
import com.example.web_api.model.File
import com.example.web_api.model.Files
import org.jetbrains.exposed.sql.ResultRow

class FileService : AbstractParenService<File>(Files, Files.branchId, Branches) {
    override fun convert(row: ResultRow): File {
        return File(
            id = row[Files.id].value,
            branchId = row[Files.branchId].value,
            path = row[Files.path],
            name = row[Files.name],
            extension = row[Files.extension],
            content = row[Files.content]
        )
    }
}