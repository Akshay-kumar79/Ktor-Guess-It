package com.akshaw.data

import io.ktor.websocket.*
import kotlinx.coroutines.isActive

class Room(
    val name: String,
    val maxPlayer: Int,
    var players: List<Player> = emptyList()
) {


    suspend fun broadcast(message: String) {
        players.forEach { player ->
            if (player.socket.isActive) {
                player.socket.send(Frame.Text(message))
            }
        }
    }

    suspend fun broadcastToAllExcept(message: String, exceptionClientId: String) {
        players.forEach { player ->
            if (player.clientId != exceptionClientId && player.socket.isActive) {
                player.socket.send(Frame.Text(message))
            }
        }
    }


}