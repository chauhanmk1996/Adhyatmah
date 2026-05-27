package com.app.adhyatmah.domain.model.profile.manage_address

data class CustomerAddressResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)