package com.app.adhyatmah.domain.model

data class GetLanguagesResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val languages: List<String>,
        val totalCount: Int
    )
}