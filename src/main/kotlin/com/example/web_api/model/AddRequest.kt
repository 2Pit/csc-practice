package com.example.web_api.model

data class AddRequest(
    val repoId: Int?,
    val libId: Int?,
    val repo: NewRepo?,
    val lib: NewLib?,
    val samples: List<SampleNew>
)

class NewRepo(val owner: String, val repo: String)

class NewLib(val name: String, val description: String, val tags: String)

data class SampleNew(
    val name: String,
    val buildSystem: String,
    val path: String,
    val description: String,
    val tags: String
)