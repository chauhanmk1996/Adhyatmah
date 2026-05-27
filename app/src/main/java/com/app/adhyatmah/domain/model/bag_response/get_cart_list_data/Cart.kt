package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class Cart(
    val checkoutUrl: String?=null,
    val cost: Cost?=null,
//    val discountCodes: List<DiscountCode>?=null,
    val discountCodes: List<String>?=null,
    val id: String?=null,
    val shipping_fee: Int?=null,
    val platform_fee: Int?=null,
    val lines: Lines?=null
)