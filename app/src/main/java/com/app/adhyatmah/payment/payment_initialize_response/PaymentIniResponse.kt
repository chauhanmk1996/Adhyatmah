package com.app.adhyatmah.payment.payment_initialize_response

data class PaymentIniResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val amount: Int,
        val success_url_app: String,
        val currency: String,
        val order_metadata: OrderMetadata,
        val razorpay: Razorpay,
        val receipt: String,
        val stripe: Stripe
    ) {
        data class OrderMetadata(
            val accessToken: String,
            val addressId: String,
            val cartId: String,
            val customer: Customer,
            val customerId: String,
            val line_items: List<LineItem>,
            val shipping_address: ShippingAddress
        ) {
            data class Customer(
                val email: String,
                val firstName: String,
                val lastName: String,
                val phone: String
            )

            data class LineItem(
                val product_title: String,
                val quantity: Int,
                val variant_id: Long
            )

            data class ShippingAddress(
                val address1: String,
                val address2: String,
                val city: String,
                val country: String,
                val first_name: String,
                val last_name: String,
                val phone: String,
                val province: String,
                val zip: String
            )
        }

        data class Razorpay(
            val amount: Int,
            val currency: String,
            val keyId: String,
            val order_id: String,
            val payment_link: PaymentLink,
            val receipt: String
        ) {
            data class PaymentLink(
                val id: String,
                val short_url: String,
            )
        }

        data class Stripe(
            val authorization_url: String,
            val order_id: String,
            val payment_link_id: String,
            val stripe_key: String
        )
    }
}