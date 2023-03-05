package com.akshaw.data.models

import com.akshaw.util.Constant

data class PlayersList(
    val players: List<PlayerData>
): BaseModel(Constant.TYPE_PLAYERS_LIST)
