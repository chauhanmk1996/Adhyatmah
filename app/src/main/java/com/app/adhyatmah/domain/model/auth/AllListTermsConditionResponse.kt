package com.app.adhyatmah.domain.model.auth

data class AllListTermsConditionResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)

{

    data class Payload(
        val privacyPolicy: PrivacyPolicy,
        val refundPolicy: RefundPolicy,
        val shippingPolicy: ShippingPolicy,
        val termsOfService: TermsOfService
    )


    data class PrivacyPolicy(
        val body: String,
        val created_at: String,
        val handle: String,
        val title: String,
        val updated_at: String,
        val url: String
    )

    data class RefundPolicy(
        val body: String,
        val created_at: String,
        val handle: String,
        val title: String,
        val updated_at: String,
        val url: String
    )

    data class ShippingPolicy(
        val body: String,
        val created_at: String,
        val handle: String,
        val title: String,
        val updated_at: String,
        val url: String
    )

    data class TermsOfService(
        val body: String,
        val created_at: String,
        val handle: String,
        val title: String,
        val updated_at: String,
        val url: String
    )



}