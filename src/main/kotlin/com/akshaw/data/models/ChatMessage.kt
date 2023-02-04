package com.akshaw.data.models

import com.akshaw.util.Constant

data class ChatMessage(
    val from: String,
    val roomName: String,
    val message: String,
    val timeStamp: Long
): BaseModel(Constant.TYPE_CHAT_MESSAGE)
