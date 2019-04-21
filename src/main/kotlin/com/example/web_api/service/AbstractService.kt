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

    open fun get(params: Map<String, List<String>>): List<T> {
        val ids = params["ids"]
        val afterId = params["after_id"]?.first()

        val query = table.selectAll().limit(50)

        if (afterId != null) { // TODO fix
            query.andWhere { table.id gt afterId.toInt() }
        }

        ids?.apply {
            query.andWhere { table.id inList (ids.map { it.toInt() }) }
        }

        return transaction { query.map { convert(it) } }
    }

    protected abstract fun convert(row: ResultRow): T

    private infix fun <T : Comparable<T>> Column<EntityID<T>>.gt(t: T): Op<Boolean> = GreaterOp(this, wrap(t))
}