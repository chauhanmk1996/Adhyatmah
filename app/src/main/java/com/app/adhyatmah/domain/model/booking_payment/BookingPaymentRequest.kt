package com.app.adhyatmah.domain.model.booking_payment

data class BookingPaymentRequest(
    val bookingId: String?=null,
    val currency: String?=null
)