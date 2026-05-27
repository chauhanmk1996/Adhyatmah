package com.app.adhyatmah.domain.model.bag_response.apply_coupons.remove_coupons

data class RemoveCouponsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)