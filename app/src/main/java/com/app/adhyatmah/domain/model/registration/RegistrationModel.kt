package com.app.adhyatmah.domain.model.registration

data class RegistrationModel(
    val firstName: String?=null,
    val lastName: String?=null,
    val email: String?=null,
    val mobile: String?=null,
    val otp: String?=null,
    val password:String?=null,
    val acceptsMarketing: Boolean?=null,
    val gender: String?=null,
    val role: String? = null,
    val commission: Int? = null,
    val about: String? = null,
    val gotra: String? = null,
    val deviceType: String? = null,
    val deviceToken: String? = null,
    var aadhar: String? = null,
    val language: List<String>? = null,

    )