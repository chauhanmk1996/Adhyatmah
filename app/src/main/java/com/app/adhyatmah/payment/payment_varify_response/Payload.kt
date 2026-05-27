package com.app.adhyatmah.payment.payment_varify_response

data class Payload(
    val order: Order,
    val payment_details: PaymentDetails
)