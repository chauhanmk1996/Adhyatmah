package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class Payload(
    val cart: Cart?=null,
    val discountInfo: DiscountInfo?=null
)