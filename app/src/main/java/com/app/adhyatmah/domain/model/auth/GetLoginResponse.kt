package com.app.adhyatmah.domain.model.auth

data class GetLoginResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)

{
    data class Payload(
        val accessToken: String,
        val expiresAt: String,
        val errors: List<Errors>
    )
    data class Errors(
        val message:String,
        val field:String,
            )
}