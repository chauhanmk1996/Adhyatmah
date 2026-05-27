package com.app.adhyatmah.domain.model.search_list_response.search_list_request

data class SearchListRequest(
    val first: Int,
    val query: String,
    val sortKey: String,
    val types: List<String>
)