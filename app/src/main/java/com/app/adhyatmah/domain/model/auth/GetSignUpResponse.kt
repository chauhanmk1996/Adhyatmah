package com.app.adhyatmah.domain.model.auth

import com.app.adhyatmah.domain.model.profile.get_profile.User
import com.app.adhyatmah.domain.model.verify_otp.VerifyOtpResponse.Payload.Customer.Cover

data class GetSignUpResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {

    data class Payload(
        val customer: User,
        val accessToken: String,
        val expiresAt: String,
        val isVendor: Boolean,
        val isUser: Boolean,
        val role: String,
    )

    data class Customer(
        val acceptsMarketing: Boolean,
        val email: String,
        val firstName: String,
        val id: String,
        val lastName: String,
        val phone: String,
    )

}