package com.example.web_api.service

import com.example.web_api.model.Branch
import com.example.web_api.model.Branches
import com.example.web_api.model.Repositories
import org.jetbrains.exposed.sql.ResultRow

object BranchService : AbstractParenService<Branch>(Branches, Branches.repoId, Repositories) {
    override fun convert(row: ResultRow): Branch {
        return Branch(
            id = row[Branches.id].value,
            repoId = row[Branches.repoId].value,
            name = row[Branches.name],
            sha = row[Branches.sha]
        )
    }
}