package com.akshaw.data.models

import com.akshaw.util.Constant

data class DrawAction(
    val action: String
): BaseModel(Constant.TYPE_DRAW_ACTION){

    companion object{
        const val ACTION_UNDO = "ACTION_UNDO"
    }

}
