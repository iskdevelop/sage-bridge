package com.iskportal.objs

import kotlinx.serialization.Serializable


@Serializable
data class ExpressionResponse(
    val type: OutputType = OutputType.RAW,
    val value: String,
    val success: Boolean,
    val error: String?,
    val executionTimeMs: Long

)