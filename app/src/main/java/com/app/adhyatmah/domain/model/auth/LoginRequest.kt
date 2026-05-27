package com.app.adhyatmah.domain.model.auth

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceType: String? = null,
    val deviceToken: String? = null,
)
