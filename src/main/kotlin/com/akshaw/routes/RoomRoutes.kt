package com.akshaw.routes

import com.akshaw.data.Room
import com.akshaw.data.models.BasicApiResponse
import com.akshaw.data.models.CreateRoomRequest
import com.akshaw.data.models.RoomResponse
import com.akshaw.server
import com.akshaw.util.Constant
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createRoomRoute(){
    route("/api/createRoom"){
        post {
            val roomRequest = try {
                call.receiveNullable<CreateRoomRequest>()
            } catch (cause: ContentTransformationException) {
                application.log.debug("Conversion failed, null returned", cause)
                null
            }

            if (roomRequest == null){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (server.rooms[roomRequest.name] != null){
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "Room already exists")
                )
                return@post
            }

            if (roomRequest.maxPlayers < 2){
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "Minimum room size is 2")
                )
                return@post
            }

            if (roomRequest.maxPlayers > Constant.MAX_ROOM_SIZE){
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(false, "Maximum room size is ${Constant.MAX_ROOM_SIZE}")
                )
                return@post
            }

            val room = Room(
                roomRequest.name,
                roomRequest.maxPlayers
            )

            server.rooms[roomRequest.name] = room
            println("Room created: ${roomRequest.name}")

            call.respond(HttpStatusCode.OK, BasicApiResponse(true))
        }
    }
}

fun Route.getRoomsRoute(){
    route("/api/getRooms"){
        get {
            val searchQuery = call.parameters["searchQuery"]

            if (searchQuery == null){
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val roomResult = server.rooms.filterKeys {
                it.contains(searchQuery, ignoreCase = true)
            }

            val roomResponse = roomResult.values.map {
                RoomResponse(it.name, it.maxPlayer, it.players.size)
            }.sortedBy { it.name }

            call.respond(HttpStatusCode.OK, roomResponse)
        }
    }
}

fun Route.joinRoomRoute(){
    route("api/joinRoom"){
        get {
            val userName = call.parameters["userName"]
            val roomName = call.parameters["roomName"]

            if (userName == null || roomName == null){
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val room = server.rooms[roomName]

            when{
                room == null -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(false, "Room not found")
                    )
                }

                room.containPlayer(userName) -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(false, "A player with $userName already in the room")
                    )
                }

                room.players.size >= room.maxPlayer -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(false, "This room is already full")
                    )
                }

                else -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse(true)
                    )
                }
            }
        }
    }
}