package com.example.git

object GitHubUrlParser {
    private val basicR = "^https://github.com/(?<owner>\\w+)/(?<repo>\\w+)$".toRegex()

    private val pathRegexp =
        "^https://github.com/(?<owner>\\w+)/(?<repo>\\w+)/tree/(?<branch>\\w+)/(?<path>[\\w/]+)$".toRegex()

    private val fileRegexp =
        "^https://github.com/(?<owner>\\w+)/(?<repo>\\w+)/blob/(?<branch>\\w+)(/(?<path>[\\w/]+))??/(?<file>[\\w\\-. ]+)$"
            .toRegex()

    fun parse(url: String): GitHubInfo? {
        var matchEntire = fileRegexp.matchEntire(url)
        if (matchEntire != null) {
            return GitHubInfo(
                matchEntire.groups["owner"]!!.value,
                matchEntire.groups["repo"]!!.value,
                matchEntire.groups["branch"]!!.value,
                matchEntire.groups["path"]?.value ?: "",
                matchEntire.groups["file"]!!.value
            )
        }

        matchEntire = pathRegexp.matchEntire(url)
        if (matchEntire != null) {
            return GitHubInfo(
                matchEntire.groups["owner"]!!.value,
                matchEntire.groups["repo"]!!.value,
                matchEntire.groups["branch"]!!.value,
                matchEntire.groups["path"]!!.value
            )
        }

        matchEntire = basicR.matchEntire(url)
        if (matchEntire != null) {
            return GitHubInfo(
                matchEntire.groups["owner"]!!.value,
                matchEntire.groups["repo"]!!.value
            )
        }
        return null
    }
}


data class GitHubInfo(
    val owner: String,
    val repo: String,
    val branch: String? = null,
    val path: String? = null,
    val file: String? = null
)