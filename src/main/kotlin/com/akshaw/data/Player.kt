package com.akshaw.data

import io.ktor.websocket.*

data class Player(
    val userName: String,
    var socket: WebSocketSession,
    val clientId: String,
    var isDrawing: Boolean,
    var score: Int = 0,
    var rank: Int = 0
)
