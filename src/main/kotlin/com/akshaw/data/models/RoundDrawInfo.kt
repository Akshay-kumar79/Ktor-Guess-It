package com.akshaw.data.models

import com.akshaw.util.Constant

data class RoundDrawInfo(
    val data: List<String>
): BaseModel(Constant.TYPE_CUR_ROUND_DRAW_INFO)
