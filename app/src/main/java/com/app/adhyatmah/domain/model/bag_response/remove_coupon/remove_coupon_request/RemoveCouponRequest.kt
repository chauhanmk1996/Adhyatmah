package com.app.adhyatmah.domain.model.bag_response.remove_coupon.remove_coupon_request

data class RemoveCouponRequest(
    var cartId: String?="",
    var discountCode: String?=""
)