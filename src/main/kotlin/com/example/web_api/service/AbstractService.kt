package com.example.web_api.service

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

    open fun get(params: Map<String, List<String>>, upperQuery: Query?): List<T> {
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

    override fun get(params: Map<String, List<String>>, upperQuery: Query?): List<T> {
        val query = upperQuery ?: buildQuery()
        val parentId = params["parent_id"]?.first()
        parentId?.apply {
            query.andWhere { parentTable.id eq parentId.toInt() }
        }
        return super.get(params, query)
    }

    private fun buildQuery(): Query {
        return (table innerJoin parentTable).select { parentColumn eq parentTable.id }.limit(50)
    }
}