import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.ContentsService


fun main() {
    val client = GitHubClient()
    client.setOAuth2Token(Property.githubToken)

    val contentService = ContentsService()
    val samples = contentService.getContents(
        RepositoryId.createFromUrl("https://github.com/spekframework/spek"),
        "samples"
    )

    for (sample in samples) {
        println(sample.name)
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
