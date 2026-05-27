package com.app.adhyatmah.domain.model.search_list_response.get_search_types_response

data class Payload(
    val first: Int,
    val query: String,
    val sortKey: String,
    val types: List<String>
)