package com.app.adhyatmah.domain.model.booking_payment

data class BookingPaymentResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int,
)

data class Payload(
    val razorpay: Razorpay,
    val success_url_app: String,
)

data class Razorpay(
    val payment_link: PaymentLink,
)

data class PaymentLink(
    val short_url: String,
)