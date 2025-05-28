package com.iskportal

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Sage Bridge is running!")
        }

        get("/health"){
            call.respondText("OK")
        }


        // Means of interacting with the server for mathematical expression handling
        post("/batch") {
            call.respondText("Post to batch")
        }
        post("/execute") {
            call.respondText("Post to execute")
        }

    }
}
