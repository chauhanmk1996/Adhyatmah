package com.app.adhyatmah.domain.model.bag_response.apply_coupons.remove_coupons_req

data class RemoveCouponsRequest(
    val cartId: String,
    val discountCode: String
)