package com.app.adhyatmah.domain.model.bag_response.apply_coupons.remove_coupons

data class Payload(
    val discountCodes: List<Any>,
    val id: String
)