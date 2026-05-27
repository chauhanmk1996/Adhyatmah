package com.app.adhyatmah.payment.payment_initialize_response

data class CheckoutOptions(
    val amount: Int,
    val callback_url: String,
    val currency: String,
    val description: String,
    val key: String,
    val name: String,
    val notes: Notes,
    val order_id: String,
    val prefill: Prefill,
    val theme: Theme
)