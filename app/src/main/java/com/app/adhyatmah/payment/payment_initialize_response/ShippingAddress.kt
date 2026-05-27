package com.app.adhyatmah.payment.payment_initialize_response

data class ShippingAddress(
    val address1: String,
    val address2: String,
    val city: String,
    val country: String,
    val first_name: String,
    val last_name: String,
    val phone: String,
    val province: String,
    val zip: String
)