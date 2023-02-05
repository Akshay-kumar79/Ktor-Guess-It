package com.akshaw.data.models

import com.akshaw.data.Room
import com.akshaw.util.Constant

data class PhaseChange(
    var phase: Room.Phase?,
    var time: Long,
    val drawingPlayer: String? = null
): BaseModel(Constant.TYPE_PHASE_CHANGE)
