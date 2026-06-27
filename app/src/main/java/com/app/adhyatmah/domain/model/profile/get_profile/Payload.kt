package com.app.adhyatmah.domain.model.profile.get_profile

import com.app.adhyatmah.domain.model.verify_otp.VerifyOtpResponse.Payload.Customer.Cover
import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("customer", alternate = ["user"])
    val user: User? = null,
    val url: String? = null,
)

data class User(
    val id: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val cover: Cover? = null,

    )