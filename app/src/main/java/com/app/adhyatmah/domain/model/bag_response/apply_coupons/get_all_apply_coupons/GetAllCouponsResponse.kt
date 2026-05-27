package com.app.adhyatmah.domain.model.bag_response.apply_coupons.get_all_apply_coupons

data class GetAllCouponsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)