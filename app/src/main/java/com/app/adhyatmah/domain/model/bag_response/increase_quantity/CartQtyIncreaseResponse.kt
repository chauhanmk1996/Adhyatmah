package com.app.adhyatmah.domain.model.bag_response.increase_quantity

data class CartQtyIncreaseResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)