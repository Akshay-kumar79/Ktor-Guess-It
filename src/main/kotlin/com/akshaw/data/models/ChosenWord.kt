package com.akshaw.data.models

import com.akshaw.util.Constant

data class ChosenWord(
    val chosenWord: String,
    val roomName: String
): BaseModel(Constant.TYPE_CHOSEN_WORD)
