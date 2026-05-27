package com.app.adhyatmah.payment.payment_varify_response

data class DefaultAddress(
    val company: Any,
    val country: String,
    val country_code: String,
    val country_name: String,
    val customer_id: Long,
    val default: Boolean,
    val id: Long,
    val province: String,
    val province_code: String
)