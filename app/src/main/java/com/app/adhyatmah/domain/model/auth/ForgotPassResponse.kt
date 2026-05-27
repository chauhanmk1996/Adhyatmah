package com.app.adhyatmah.domain.model.auth

data class ForgotPassResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)

{

    class Payload
}