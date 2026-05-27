package com.app.adhyatmah.domain.model.booking_payment

data class BookingPaymentResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val amount: Int,
        val booking: Booking,
        val currency: String,
        val success_url_app: String,
        val razorpay: Razorpay,
        val receipt: String,
        val stripe: Stripe
    ) {
        data class Booking(
            val bookingID: String,
            val id: String,
            val poojaType: String
        )

        data class Razorpay(
            val amount: Int,
            val currency: String,
            val keyId: String,
            val orderId: String,
            val payment_link: PaymentLink,
            val receiptId: String
        ) {
            data class PaymentLink(
                val id: String,
                val short_url: String
            )
        }

        data class Stripe(
            val payment_intent_id: String,
            val payment_link_id: String,
            val payment_url: String
        )
    }
}