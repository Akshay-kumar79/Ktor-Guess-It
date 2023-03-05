package com.akshaw.util

import com.akshaw.data.models.ChatMessage
import io.ktor.util.*

fun ChatMessage.matchesWord(word: String): Boolean{
    return message.lowercase().trim() == word.lowercase().trim()
}