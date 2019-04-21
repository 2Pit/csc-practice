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

            SchemaUtils.create(Branches, Files, Libraries, Repositories, Samples, Snapshots)

            Branches.insert {
                it[repoId] = "repo1"
                it[name] = "name1"
                it[sha] = "sha1"
            }
            Branches.insert {
                it[repoId] = "repo2"
                it[name] = "name2"
                it[sha] = "sha2"
            }
            Branches.insert {
                it[repoId] = "repo3"
                it[name] = "name3"
                it[sha] = "sha3"
            }

            Files.insert {
                it[path] = "path1"
                it[name] = "name1"
                it[extension] = "extension1"
                it[content] = "content1"
            }
            Files.insert {
                it[path] = "path2"
                it[name] = "name2"
                it[extension] = "extension2"
                it[content] = "content2"
            }
            Files.insert {
                it[path] = "path3"
                it[name] = "name3"
                it[extension] = "extension3"
                it[content] = "content3"
            }

            Libraries.insert {
                it[ownerRepoId] = "ownerRepo1"
                it[name] = "name1"
                it[description] = "description1"
                it[tags] = "tags1"
            }
            Libraries.insert {
                it[ownerRepoId] = "ownerRepo2"
                it[name] = "name2"
                it[description] = "description2"
                it[tags] = "tags2"
            }
            Libraries.insert {
                it[ownerRepoId] = "ownerRepo3"
                it[name] = "name3"
                it[description] = "description3"
                it[tags] = "tags3"
            }

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

            Samples.insert {
                it[libId] = "lib1"
                it[name] = "name1"
                it[buildSystem] = "buildSystem1"
                it[path] = "path1"
                it[description] = "description1"
                it[tags] = "tags1"
            }
            Samples.insert {
                it[libId] = "lib2"
                it[name] = "name2"
                it[buildSystem] = "buildSystem2"
                it[path] = "path2"
                it[description] = "description2"
                it[tags] = "tags2"
            }
            Samples.insert {
                it[libId] = "lib3"
                it[name] = "name3"
                it[buildSystem] = "buildSystem3"
                it[path] = "path3"
                it[description] = "description3"
                it[tags] = "tags3"
            }

            Snapshots.insert {
                it[repoId] = EntityID(1, Samples)
                it[branch] = "branch1"
            }
            Snapshots.insert {
                it[repoId] = EntityID(2, Samples)
                it[branch] = "branch2"
            }
            Snapshots.insert {
                it[repoId] = EntityID(3, Samples)
                it[branch] = "branch3"
            }
        }
    }
}