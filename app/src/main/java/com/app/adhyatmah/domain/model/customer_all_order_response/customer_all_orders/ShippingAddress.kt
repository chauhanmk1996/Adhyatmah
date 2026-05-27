package com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders

data class ShippingAddress(
    val country: String,
    val country_code: String,
    val customer_name: String?=null,
    val province: String,
    val province_code: String
)