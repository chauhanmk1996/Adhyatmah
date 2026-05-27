package com.app.adhyatmah.domain.model.auth

data class SignUpRequest(
    val firstName: String,
    val lastName : String,
    val email : String,
    val password : String,
    val gender : String?=null,
    val phone : String,
    val role : String,
    val deviceType: String? = null,
    val deviceToken: String? = null,

// "phone": "+15146669999",
//   "acceptsMarketing": true
)
