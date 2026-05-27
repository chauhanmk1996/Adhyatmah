package com.app.adhyatmah.domain.model.filter.filter_response

data class FilterResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)