package com.app.adhyatmah.payment.payment_initialize_response

data class Payload(
    val amount: Double,
    val authorization_url: String,
    val checkout_options: CheckoutOptions,
    val currency: String,
    val order_id: String,
    val order_metadata: OrderMetadata,
    val payment_link_id: String,
    val razorpay_key: String,
    val receipt: String
)