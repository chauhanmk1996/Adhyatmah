package com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response

data class BillingAddress(
    val country: String,
    val country_code: String,
    val province: String,
    val province_code: String
)