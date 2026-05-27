package com.app.adhyatmah.domain.model.auth

data class LogoutResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)

{
    data class Payload(
        val deletedAccessToken: String,
        val deletedCustomerAccessTokenId: String
    )
}