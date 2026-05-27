package com.app.adhyatmah.domain.model.bag_response.apply_coupons.apply_coupons

data class Payload(
//    val discountCodes: List<DiscountCode>?=null,
    val discountCodes: List<String>?=null,
    val id: String?=null,
    val discount: Discount?=null,
    val cartTotal: String?=null,
    val discountedTotal: String?=null,
)
data class Discount(
    val amount: String?=null,
    val type: String?=null,
    val code: String?=null,
    val percentage: String?=null,
    val fixedAmount: String?=null,
)