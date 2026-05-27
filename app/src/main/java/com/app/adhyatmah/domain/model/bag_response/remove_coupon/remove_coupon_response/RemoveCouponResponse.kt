package com.app.adhyatmah.domain.model.bag_response.remove_coupon.remove_coupon_response

data class RemoveCouponResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)