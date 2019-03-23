package dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Projects : IntIdTable() {
    val sha = varchar("sha", 40)
    val name = varchar("name", 50)
    val url = varchar("url", 300)
    val type = varchar("type", 10) // TODO enum = {Lib | Framework}
    // val owner
    // val tags
}

class Project(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Project>(Projects)

    var sha by Projects.sha
    var name by Projects.name
    var url by Projects.url
    var type by Projects.type
}

object Samples : IntIdTable() {
    val project = reference("project", Projects)
    val name = varchar("name", 50)
    val path = varchar("path", 300)
    val buildSystem = varchar("build_system", 10) // TODO enum = {Maven | Gradle | GradleKt}
}

class Sample(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Sample>(Samples)

    var project by Project referencedOn Samples.project
    var name by Samples.name
    var path by Samples.path
    var buildSystem by Samples.buildSystem
}

object Files : IntIdTable() {
    val sha = varchar("sha", 40)
    val sample = reference("sample", Samples)
    val path = varchar("path", 300)
    val name = varchar("name", 50)
    //    val encode = varchar("encode", 10) // TODO enum = {Base64, UTF-8}
    val extension = varchar("extension", 10)
    val content = text("content")
}

class File(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<File>(Files)

    var sha by Files.sha
    var sample by Sample referencedOn Files.sample
    var path by Files.path
    var name by Files.name
    var extension by Files.extension
    var content by Files.content
}










