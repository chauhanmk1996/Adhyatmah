package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class GetCartListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)