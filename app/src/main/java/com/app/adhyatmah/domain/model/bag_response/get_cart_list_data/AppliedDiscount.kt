package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class AppliedDiscount(
    val amount: Double?=null,
    val code: String?=null,
    val currencyCode: String?=null,
    val discountPercentage: String?=null,
    val type: String?=null
)