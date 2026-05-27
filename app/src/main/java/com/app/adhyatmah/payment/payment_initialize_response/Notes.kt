package com.app.adhyatmah.payment.payment_initialize_response

data class Notes(
    val accessToken: String,
    val addressId: String,
    val cartId: String,
    val customerId: String
)