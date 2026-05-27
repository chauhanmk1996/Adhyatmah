package com.app.adhyatmah.domain.model.delete_address.response_delete_address

data class DeleteAddressResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)