package com.akshaw.data.models

data class PlayerData(
    val userName: String,
    var isDrawing: Boolean = false,
    var score: Int = 0,
    var rank: Int = 0
)
