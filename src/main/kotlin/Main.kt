import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.ContentsService
import org.eclipse.egit.github.core.service.DataService


fun main() {
    val client = GitHubClient()
    client.setOAuth2Token(Property.githubToken)

    val contentService = ContentsService()
    val samples = contentService.getContents(
//        RepositoryId.createFromUrl("https://github.com/spekframework/spek"),
        RepositoryId.createFromUrl("https://github.com/2Pit/csc-practice"),
        ""
    )


    val repo = RepositoryId.create("2Pit", "csc-practice")

    val dataService = DataService()
    val tree = dataService.getTree(repo, "74be18d3e65f9445b7c0d0abaa8827ab967e1623", true)

//    for (sample in samples) {
//        println("${sample.name} ${sample.content}")
//    }

    println(tree.url)
    for (t in tree.tree) {
        println(t.path)
    }
}


object Property {
    val props: Map<String, String> = Property.javaClass.getResourceAsStream("prop.txt")
        .reader()
        .readLines()
        .map { it.split("=") }
        .associate { it[0] to it[1] }

    val githubToken = props["github.oauth2.token"]
}
