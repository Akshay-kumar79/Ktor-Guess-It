package com.akshaw.data

import com.akshaw.data.models.Ping
import com.akshaw.gson
import com.akshaw.server
import com.akshaw.util.Constant
import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Player(
    val userName: String,
    var socket: WebSocketSession,
    val clientId: String,
    var isDrawing: Boolean = false,
    var score: Int = 0,
    var rank: Int = 0
){
    private var pingJob: Job? = null

    private var pingTime = 0L
    private var pongTime = 0L

    var isOnline = true

    fun startPinging(){
        pingJob?.cancel()
        pingJob = GlobalScope.launch {
            while (true){
                sendPing()
                delay(Constant.PING_FREQUENCY)
            }
        }
    }

    private suspend fun sendPing() {
        pingTime = System.currentTimeMillis()
        socket.send(Frame.Text(gson.toJson(Ping())))
        delay(Constant.PING_FREQUENCY)
        if (pingTime - pongTime > Constant.PING_FREQUENCY){
            isOnline = false
            server.playerLeft(clientId)
            pingJob?.cancel()
        }
    }

    fun receivedPong(){
        pongTime = System.currentTimeMillis()
        isOnline = true
    }

    fun disconnect(){
        pingJob?.cancel()
    }
}
