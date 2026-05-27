package com.app.adhyatmah.payment.payment_initialize_response

data class OrderMetadata(
    val accessToken: String,
    val addressId: String,
    val cartId: String,
    val customer: Customer,
    val customerId: String,
    val line_items: List<LineItem>,
    val shipping_address: ShippingAddress
)