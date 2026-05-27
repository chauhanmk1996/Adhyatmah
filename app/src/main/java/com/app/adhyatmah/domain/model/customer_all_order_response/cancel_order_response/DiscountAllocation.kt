package com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response

data class DiscountAllocation(
    val amount: String,
    val amount_set: AmountSet,
    val discount_application_index: Int
)