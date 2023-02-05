package com.akshaw.plugins

import com.akshaw.routes.createRoomRoute
import com.akshaw.routes.gameWebSocketRoute
import com.akshaw.routes.getRoomsRoute
import com.akshaw.routes.joinRoomRoute
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {

    install(Routing){
        createRoomRoute()
        getRoomsRoute()
        joinRoomRoute()
        gameWebSocketRoute()
    }

}
