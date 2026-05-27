package com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response

data class DiscountApplication(
    val allocation_method: String,
    val code: String,
    val target_selection: String,
    val target_type: String,
    val type: String,
    val value: String,
    val value_type: String
)