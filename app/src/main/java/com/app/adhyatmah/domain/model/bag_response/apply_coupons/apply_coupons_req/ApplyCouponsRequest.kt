package com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons_req

data class ApplyCouponsRequest(
    val cartId: String,
    val discountCode: String
)