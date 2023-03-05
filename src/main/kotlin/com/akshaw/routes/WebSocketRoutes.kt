package com.akshaw.routes

import com.akshaw.data.Player
import com.akshaw.data.Room
import com.akshaw.data.models.*
import com.akshaw.gson
import com.akshaw.server
import com.akshaw.session.DrawingSession
import com.akshaw.util.Constant
import com.google.gson.JsonParser
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach


fun Route.gameWebSocketRoute(){
    route("/ws/draw"){
        standardWebSocket { socket, clientId, message, payload ->
            when(payload){
                is JoinRoomHandshake -> {
                    val room = server.rooms[payload.roomName]
                    if (room == null){
                        val gameError = GameError(GameError.ERROR_ROOM_NOT_FOUND)
                        socket.send(Frame.Text(gson.toJson(gameError)))
                        return@standardWebSocket
                    }

                    val player = Player(
                        payload.userName,
                        socket,
                        payload.clientId
                    )

                    server.playerJoined(player)
                    if (!room.containPlayer(player.userName)){
                        room.addPlayer(player.clientId, player.userName, socket)
                    }else{
                        val playerInRoom = room.players.find { it.clientId == clientId }
                        playerInRoom?.socket = socket
                        playerInRoom?.startPinging()
                    }
                }

                is DrawData -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    if (room.phase == Room.Phase.GAME_RUNNING){
                        room.broadcastToAllExcept(message, clientId)
                        room.addSerializedDrawInfo(message)
                    }
                    room.lastDrawData = payload
                }

                is DrawAction -> {
                    val room = server.getRoomWithClientId(clientId) ?: return@standardWebSocket
                    room.broadcastToAllExcept(message, clientId)
                    room.addSerializedDrawInfo(message)
                }

                is ChosenWord -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    room.setWordAndSwitchToGameRunning(payload.chosenWord)
                }

                is ChatMessage -> {
                    val room = server.rooms[payload.roomName] ?: return@standardWebSocket
                    if (!room.checkWordAndNotifyPlayers(payload)){
                        room.broadcast(message)
                    }
                }

                is Ping -> {
                    server.players[clientId]?.receivedPong()
                }

                is DisconnectRequest -> {
                    server.playerLeft(clientId, true)
                }
            }
        }
    }
}
fun Route.standardWebSocket(
    handleFrame: suspend (
        socket: DefaultWebSocketServerSession,
        clientId: String,
        message: String,
        payload: BaseModel,
    ) -> Unit
) {
    webSocket {
        val session = call.sessions.get<DrawingSession>()

        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }

        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    val jsonObject = JsonParser.parseString(message).asJsonObject
                    val type = when (jsonObject.get("type").asString) {
                        Constant.TYPE_CHAT_MESSAGE -> ChatMessage::class.java
                        Constant.TYPE_DRAW_DATA -> DrawData::class.java
                        Constant.TYPE_ANNOUNCEMENT -> Announcement::class.java
                        Constant.TYPE_JOIN_ROOM_HANDSHAKE -> JoinRoomHandshake::class.java
                        Constant.TYPE_PHASE_CHANGE -> PhaseChange::class.java
                        Constant.TYPE_CHOSEN_WORD -> ChosenWord::class.java
                        Constant.TYPE_GAME_STATE -> GameState::class.java
                        Constant.TYPE_PING -> Ping::class.java
                        Constant.TYPE_DISCONNECT_REQUEST -> DisconnectRequest::class.java
                        Constant.TYPE_DRAW_ACTION -> DrawAction::class.java
                        else -> BaseModel::class.java
                    }
                    val payload = gson.fromJson(message, type)
                    handleFrame(this, session.clientId, message, payload)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            val playerWithClientId = server.getRoomWithClientId(session.clientId)?.players?.find {
                it.clientId == session.clientId
            }
            if (playerWithClientId != null){
                server.playerLeft(session.clientId)
            }
        }
    }
}