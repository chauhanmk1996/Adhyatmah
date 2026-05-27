package com.app.adhyatmah.domain.model.fetch_wish_data

data class FetchWishListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)