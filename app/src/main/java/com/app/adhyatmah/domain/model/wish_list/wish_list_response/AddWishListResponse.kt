package com.app.adhyatmah.domain.model.wish_list.wish_list_response

data class AddWishListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)