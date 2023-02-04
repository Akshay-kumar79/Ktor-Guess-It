package com.akshaw.data.models

import java.security.MessageDigest

data class BasicApiResponse(
    val successful: Boolean,
    val message: String? = null
)
