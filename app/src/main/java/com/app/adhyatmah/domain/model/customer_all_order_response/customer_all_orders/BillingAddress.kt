package com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders

data class BillingAddress(
    val country: String,
    val country_code: String,
    val customer_name: String,
    val province: String,
    val province_code: String
)