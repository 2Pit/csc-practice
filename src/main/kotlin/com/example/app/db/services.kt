package com.example.app.db

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.wrap
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AbstractService<T : Any>(private val table: IntIdTable) {
    fun getById(id: Int): T? {
        return transaction {
            table.select { table.id eq id }
                .mapNotNull { convert(it) }
                .singleOrNull()
        }
    }

    open fun get(params: Map<String, List<String>>, upperQuery: Query? = null): List<T> {
        val query = upperQuery ?: buildQuery()
        val ids = params["ids"]
        val afterId = params["after_id"]?.first()

        afterId?.apply {
            query.andWhere { table.id gt afterId.toInt() }
        }
        ids?.apply {
            query.andWhere { table.id inList (ids.map { it.toInt() }) }
        }

        return transaction { query.map { convert(it) } }
    }

    protected abstract fun convert(row: ResultRow): T

    private fun buildQuery() = table.selectAll().limit(50)

    private infix fun <T : Comparable<T>> Column<EntityID<T>>.gt(t: T): Op<Boolean> = GreaterOp(this, wrap(t))
}


abstract class AbstractParenService<T : Any>(
    private val table: IntIdTable,
    private val parentColumn: Column<EntityID<Int>>,
    private val parentTable: IntIdTable
) : AbstractService<T>(table) {

    abstract val parentIdKey: String

    override fun get(params: Map<String, List<String>>, upperQuery: Query?): List<T> {
        val query = upperQuery ?: buildQuery()
        val parentId = params[parentIdKey]?.first()
        parentId?.apply {
            query.andWhere { parentTable.id eq parentId.toInt() }
        }
        return super.get(params, query)
    }

    private fun buildQuery(): Query {
        return (table innerJoin parentTable).select { parentColumn eq parentTable.id }.limit(50)
    }
}

//
//object LocationService : AbstractService<RepositoryRow>(Locations) {
//    fun getBy(owner: String, name: String, branch: String): RepositoryRow? {
//        var ans: RepositoryRow? = null
//        transaction {
//            ans = Locations.select { Locations.owner eq owner }
//                .andWhere { Locations.name eq name }
//                .andWhere { Locations.branch eq branch }
//                .andWhere { Locations.path eq path}
//                .map { convert(it) }
//                .firstOrNull()
//        }
//        return ans
//    }
//}
//
//object SampleService : AbstractParenService<Sample>(Samples, Samples.repoId, Repositories) {
//    override val parentIdKey: String
//        get() = "lib_id"
//
//    override fun convert(row: ResultRow): Sample {
//        return Sample(
//            id = row[Samples.id].value,
//            repoId = row[Samples.repoId].value,
//            libId = row[Samples.libId].value,
//            name = row[Samples.name],
//            buildSystem = row[Samples.buildSystem],
//            path = row[Samples.path],
//            description = row[Samples.description],
//            topics = row[Samples.topics]
//        )
//    }
//}
//
//object SnapshotService : AbstractParenService<Snapshot>(Snapshots, Snapshots.sampleId, Samples) {
//    override val parentIdKey: String
//        get() = "sample_id"
//
//    override fun convert(row: ResultRow): Snapshot {
//        return Snapshot(
//            id = row[Snapshots.id].value,
//            sampleId = row[Snapshots.sampleId].value,
//            sha = row[Snapshots.sha],
//            status = row[Snapshots.status]
//        )
//    }
//}
//
//object FileService : AbstractParenService<File>(Files, Files.snapshotId, Snapshots) {
//    override val parentIdKey: String
//        get() = "branch_id"
//
//    override fun convert(row: ResultRow): File {
//        return File(
//            id = row[Files.id].value,
//            snapshotId = row[Files.snapshotId].value,
//            path = row[Files.path],
//            name = row[Files.name],
//            extension = row[Files.extension],
//            content = row[Files.content]
//        )
//    }
//}

object JobService : AbstractService<JobRow>(Jobs) {
    override fun convert(row: ResultRow): JobRow {
        return JobRow(
            id = row[Jobs.id].value,
            status = row[Jobs.status],
            description = row[Jobs.description],
            context = row[Jobs.context]
        )
    }

    fun create(cntx: String): Int {
        val entityID = transaction {
            Jobs.insert {
                it[context] = cntx
                it[status] = "new"
                it[description] = ""
            } get Jobs.id
        }
        return entityID!!.value
    }

    fun unpdate(id: Int, status: String, description: String = "") {
        transaction {
            Jobs.update({ Jobs.id eq id }) {
                it[Jobs.status] = status
                it[Jobs.description] = description
            }
        }
    }
}