package com.example.web_api

import com.example.web_api.new_model.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DB {
    val connect = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

    fun init() {
        transaction {
            // print sql to std-out
            addLogger(StdOutSqlLogger)


            SchemaUtils.create(Files, Libraries, Repositories, Samples, Snapshots, Jobs)

            Repositories.insert {
                it[owner] = "owner1"
                it[name] = "name1"
                it[branch] = "master"
            }
            Repositories.insert {
                it[owner] = "owner2"
                it[name] = "name2"
                it[branch] = "master"
            }
            Repositories.insert {
                it[owner] = "owner3"
                it[name] = "name3"
                it[branch] = "master"
            }

            Libraries.insert {
                it[owner] = "owner1"
                it[name] = "name1"
                it[description] = "description1"
                it[topics] = "topics1"
            }
            Libraries.insert {
                it[owner] = "owner2"
                it[name] = "name2"
                it[description] = "description2"
                it[topics] = "topics2"
            }
            Libraries.insert {
                it[owner] = "owner3"
                it[name] = "name3"
                it[description] = "description3"
                it[topics] = "topics3"
            }

            Samples.insert {
                it[libId] = EntityID(1, Libraries)
                it[repoId] = EntityID(1, Repositories)
                it[name] = "name1"
                it[buildSystem] = "buildSystem1"
                it[path] = "path1"
                it[description] = "description1"
                it[topics] = "topics1"
            }
            Samples.insert {
                it[libId] = EntityID(2, Libraries)
                it[repoId] = EntityID(2, Repositories)
                it[name] = "name2"
                it[buildSystem] = "buildSystem2"
                it[path] = "path2"
                it[description] = "description2"
                it[topics] = "topics2"
            }
            Samples.insert {
                it[libId] = EntityID(3, Libraries)
                it[repoId] = EntityID(3, Repositories)
                it[name] = "name3"
                it[buildSystem] = "buildSystem3"
                it[path] = "path3"
                it[description] = "description3"
                it[topics] = "topics3"
            }

            Snapshots.insert {
                it[sampleId] = EntityID(1, Samples)
                it[status] = "ok"
                it[sha] = "sha1"
            }
            Snapshots.insert {
                it[sampleId] = EntityID(2, Samples)
                it[status] = "ok"
                it[sha] = "sha2"
            }
            Snapshots.insert {
                it[sampleId] = EntityID(3, Samples)
                it[status] = "ok"
                it[sha] = "sha3"
            }

            Files.insert {
                it[snapshotId] = EntityID(1, Snapshots)
                it[path] = "path1"
                it[name] = "name1"
                it[extension] = "extension1"
                it[content] = "content1"
            }
            Files.insert {
                it[snapshotId] = EntityID(2, Snapshots)
                it[path] = "path2"
                it[name] = "name2"
                it[extension] = "extension2"
                it[content] = "content2"
            }
            Files.insert {
                it[snapshotId] = EntityID(3, Snapshots)
                it[path] = "path3"
                it[name] = "name3"
                it[extension] = "extension3"
                it[content] = "content3"
            }
        }
    }
}