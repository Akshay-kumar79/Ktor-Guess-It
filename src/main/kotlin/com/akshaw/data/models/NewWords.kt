package com.akshaw.data.models

import com.akshaw.util.Constant

data class NewWords(
    val newWords: List<String>
): BaseModel(Constant.TYPE_NEW_WORDS)
