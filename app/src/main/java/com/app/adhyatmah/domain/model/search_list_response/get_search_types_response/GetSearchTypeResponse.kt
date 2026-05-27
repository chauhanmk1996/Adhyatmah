package com.app.adhyatmah.domain.model.search_list_response.get_search_types_response

data class GetSearchTypeResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)