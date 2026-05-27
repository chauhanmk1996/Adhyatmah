package com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_response

data class AddtoBagResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)