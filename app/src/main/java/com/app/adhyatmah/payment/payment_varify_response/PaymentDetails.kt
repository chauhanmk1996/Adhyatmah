package com.app.adhyatmah.payment.payment_varify_response

data class PaymentDetails(
    val amount: Int,
    val authorization: Authorization,
    val channel: String,
    val currency: String,
    val paid_at: String,
    val reference: String,
    val status: String
)