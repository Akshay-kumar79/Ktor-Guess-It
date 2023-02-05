package com.akshaw.data.models

import com.akshaw.util.Constant

data class GameError(
    val errorType: Int,
): BaseModel(Constant.TYPE_GAME_ERROR){

    companion object{
        const val ERROR_ROOM_NOT_FOUND = 0
    }

}
