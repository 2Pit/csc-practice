package com.example.web_api.service

import com.example.web_api.model.Snapshot
import com.example.web_api.model.Snapshots
import org.jetbrains.exposed.sql.ResultRow

class SnapshotService : AbstractService<Snapshot>(Snapshots) {
    override fun convert(row: ResultRow): Snapshot {
        return Snapshot(
            repoId = row[Snapshots.repoId].value,
            branch = row[Snapshots.branch]
        )
    }
}