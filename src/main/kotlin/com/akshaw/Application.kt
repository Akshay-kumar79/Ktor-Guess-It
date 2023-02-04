package com.akshaw

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.akshaw.plugins.*
import com.akshaw.session.DrawingSession
import com.google.gson.Gson
import io.ktor.server.sessions.*
import io.ktor.util.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

val server = DrawingServer()
val gson = Gson()

fun Application.module() {

    install(Sessions){
        cookie<DrawingSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Plugins){
        if (call.sessions.get<DrawingSession>() == null){
            val clientId = call.parameters["client_id"] ?: ""
            call.sessions.set(DrawingSession(clientId, generateNonce()))
        }
    }

    configureSerialization()
    configureSockets()
    configureMonitoring()
    configureRouting()
}
