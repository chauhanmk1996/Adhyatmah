package com.app.adhyatmah.domain.create_order.creat_order_request

data class CreaterOrderRequest(
    var accessToken: String?="",
    var addressId: String?="",
    var cartId: String?=""
)