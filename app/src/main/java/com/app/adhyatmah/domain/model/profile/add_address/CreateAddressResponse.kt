package com.app.adhyatmah.domain.model.profile.add_address

data class CreateAddressResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)

{

    data class Payload(
        val address: Address
    )

    data class Address(
        val address1: String,
        val city: String,
        val country: String,
        val id: String
    )
}