package com.app.adhyatmah.domain.create_order.creater_order_response

data class CreateCODResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)