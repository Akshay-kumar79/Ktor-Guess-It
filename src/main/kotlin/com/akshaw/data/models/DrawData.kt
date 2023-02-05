package com.akshaw.data.models

import com.akshaw.util.Constant

data class DrawData(
    val roomName: String,
    val color: Int,
    val thickness: Float,
    val fromX: Float,
    val fromY: Float,
    val toX: Float,
    val toY: Float,
    val motionEvent: Int
): BaseModel(Constant.TYPE_DRAW_DATA)
