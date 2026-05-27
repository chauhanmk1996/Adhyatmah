package com.app.adhyatmah.domain.model.search_list_response.search_list_api_response

data class GetSearchListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)