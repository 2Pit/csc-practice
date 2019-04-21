package com.example.web_api.service

import com.example.web_api.model.Branch
import com.example.web_api.model.Branches
import org.jetbrains.exposed.sql.ResultRow

class BranchService : AbstractService<Branch>(Branches) {
    override fun convert(row: ResultRow): Branch {
        return Branch(
            repoId = row[Branches.repoId].value,
            name = row[Branches.name],
            sha = row[Branches.sha]
        )
    }
}