package com.app.adhyatmah.payment.payment

data class PaymentTypeResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: List<Payload>,
    val status: Int
)