package com.app.adhyatmah.domain.model.filter.getFilter

data class GetFilterResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)