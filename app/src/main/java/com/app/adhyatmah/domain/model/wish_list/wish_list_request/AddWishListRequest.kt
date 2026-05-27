package com.app.adhyatmah.domain.model.wish_list.wish_list_request

data class AddWishListRequest(
    val accessToken: String,
    val productId: String
)
