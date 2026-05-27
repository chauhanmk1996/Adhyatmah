package com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders

data class Payload(
    val customer: Customer,
    val orders: List<Order>?=null,
    val pagination: Any
)