package com.app.adhyatmah.domain.model.shipping_response

data class ShippingUrlResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)