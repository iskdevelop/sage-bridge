package com.iskportal.objs

import kotlinx.serialization.Serializable

@Serializable
data class ExpressionRequest(
    val code: String,
    val outputType: OutputType
)
