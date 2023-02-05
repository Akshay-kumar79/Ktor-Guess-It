package com.akshaw.data.models

import com.akshaw.util.Constant

data class JoinRoomHandshake(
    val userName: String,
    val roomName: String,
    val clientId: String,
): BaseModel(Constant.TYPE_JOIN_ROOM_HANDSHAKE)
