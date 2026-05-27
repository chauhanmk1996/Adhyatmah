package com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response

data class Payload(
    val notice: String,
    val order: Order
)