package com.app.adhyatmah.domain.model.wish_list.remove_wish_list

data class RemoveWishListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)