package com.example.web_api

import com.example.web_api.model.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DB {
    val connect = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

    fun init() {
        transaction {
            // print sql to std-out
            addLogger(StdOutSqlLogger)


            SchemaUtils.create(Branches, Files, Libraries, Repositories, Samples, Jobs)

            Repositories.insert {
                it[owner] = "owner1"
                it[repo] = "repo1"
            }
            Repositories.insert {
                it[owner] = "owner2"
                it[repo] = "repo2"
            }
            Repositories.insert {
                it[owner] = "owner3"
                it[repo] = "repo3"
            }

            Branches.insert {
                it[repoId] = EntityID(1, Repositories)
                it[name] = "name1"
                it[sha] = "sha1"
            }
            Branches.insert {
                it[repoId] = EntityID(2, Repositories)
                it[name] = "name2"
                it[sha] = "sha2"
            }
            Branches.insert {
                it[repoId] = EntityID(3, Repositories)
                it[name] = "name3"
                it[sha] = "sha3"
            }

            Files.insert {
                it[branchId] = EntityID(1, Branches)
                it[path] = "path1"
                it[name] = "name1"
                it[extension] = "extension1"
                it[content] = "content1"
            }
            Files.insert {
                it[branchId] = EntityID(2, Branches)
                it[path] = "path2"
                it[name] = "name2"
                it[extension] = "extension2"
                it[content] = "content2"
            }
            Files.insert {
                it[branchId] = EntityID(3, Branches)
                it[path] = "path3"
                it[name] = "name3"
                it[extension] = "extension3"
                it[content] = "content3"
            }

            Libraries.insert {
                it[ownerRepoId] = EntityID(1, Repositories)
                it[path] = "path1"
                it[name] = "name1"
                it[description] = "description1"
                it[tags] = "tags1"
            }
            Libraries.insert {
                it[ownerRepoId] = EntityID(2, Repositories)
                it[path] = "path2"
                it[name] = "name2"
                it[description] = "description2"
                it[tags] = "tags2"
            }
            Libraries.insert {
                it[ownerRepoId] = EntityID(3, Repositories)
                it[path] = "path3"
                it[name] = "name3"
                it[description] = "description3"
                it[tags] = "tags3"
            }

            Samples.insert {
                it[libId] = EntityID(1, Libraries)
                it[name] = "name1"
                it[buildSystem] = "buildSystem1"
                it[path] = "path1"
                it[description] = "description1"
                it[tags] = "tags1"
            }
            Samples.insert {
                it[libId] = EntityID(2, Libraries)
                it[name] = "name2"
                it[buildSystem] = "buildSystem2"
                it[path] = "path2"
                it[description] = "description2"
                it[tags] = "tags2"
            }
            Samples.insert {
                it[libId] = EntityID(3, Libraries)
                it[name] = "name3"
                it[buildSystem] = "buildSystem3"
                it[path] = "path3"
                it[description] = "description3"
                it[tags] = "tags3"
            }
        }
    }
}