package com.akshaw.data.models

import com.akshaw.util.Constant

data class GameState(
    val drawingPlayer: String,
    val word: String
): BaseModel(Constant.TYPE_GAME_STATE)
