package com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details

data class GetMyOrderDetailsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)