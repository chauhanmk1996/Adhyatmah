package com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders

data class ShippingLine(
    val carrier_identifier: Any,
    val code: String,
    val discount_allocations: List<Any?>,
    val discounted_price: String,
    val discounted_price_set: DiscountedPriceSet,
    val id: Long,
    val is_removed: Boolean,
    val phone: Any,
    val price: String,
    val price_set: PriceSet,
    val requested_fulfillment_service_id: Any,
    val source: String,
    val tax_lines: List<Any?>,
    val title: String
)