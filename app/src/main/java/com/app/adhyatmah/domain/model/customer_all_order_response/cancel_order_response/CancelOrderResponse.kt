package com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response

data class CancelOrderResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)