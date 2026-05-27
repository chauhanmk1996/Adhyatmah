package com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons

data class ApplyCouponsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)