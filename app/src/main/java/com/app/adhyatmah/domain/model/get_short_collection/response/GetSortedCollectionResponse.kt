package com.app.adhyatmah.domain.model.get_short_collection.response

data class GetSortedCollectionResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)