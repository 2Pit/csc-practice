package com.example.web_api.web

import com.example.web_api.service.SampleService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.sample(sampleService: SampleService) {

    route("/samples") {

        get("/") {
            call.respond(sampleService.getAllSamples())
        }

        get("/{id}") {
            val sample = sampleService.getSample(call.parameters["id"]!!.toInt())
            if (sample == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(sample)
            }
        }
    }
}
