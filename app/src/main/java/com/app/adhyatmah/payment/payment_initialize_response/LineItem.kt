package com.app.adhyatmah.payment.payment_initialize_response

data class LineItem(
    val product_title: String,
    val quantity: Int,
    val variant_id: Long
)