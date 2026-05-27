package com.app.adhyatmah.payment.payment_varify_response

data class PaymentVerifyResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)