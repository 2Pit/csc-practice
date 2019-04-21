package com.example.web_api.web

import com.example.web_api.service.AbstractService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.util.toMap

fun Route.myRoute(path: String, service: AbstractService<*>) {
    route(path) {

        get("/") {
            val params = call.parameters.toMap()
            call.respond(service.get(params, null))
        }

        get("/{id}") {
            val sample = service.getById(call.parameters["id"]!!.toInt())
            if (sample == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(sample)
            }
        }
    }
}