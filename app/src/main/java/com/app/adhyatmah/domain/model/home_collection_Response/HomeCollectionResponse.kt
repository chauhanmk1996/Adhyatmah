package com.app.adhyatmah.domain.model.home_collection_Response

data class HomeCollectionResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)