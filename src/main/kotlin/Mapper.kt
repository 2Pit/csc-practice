import org.eclipse.egit.github.core.RepositoryContents
import java.util.*


fun String.base64toUtf8(): String {
    return String(Base64.getDecoder().decode(this))
}

fun convert(content: RepositoryContents): Node {
    return when (content.type) {
        "file" -> File(content.name, content.sha, content.content.base64toUtf8())
        "dir" -> Dir(content.name, content.sha)
        else -> throw IllegalArgumentException()
    }
}

data class Sample(
    val sha: String,
    val name: String,
    val description: String,
    val path: String,
    val root: Node?
)

enum class Type {
    FILE, DIR
}

interface Node {
    //    parent & sons
    val name: String
    val sha: String
    val type: Type
}

class File(
    override val name: String,
    override val sha: String,
    val content: String
) : Node {
    override val type: Type
        get() = Type.FILE
}

class Dir(
    override val name: String,
    override val sha: String
) : Node {
    override val type: Type
        get() = Type.DIR
}