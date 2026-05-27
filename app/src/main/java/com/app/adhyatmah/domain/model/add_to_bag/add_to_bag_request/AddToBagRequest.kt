package com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_request

data class AddToBagRequest(
    var accessToken: String?="",
    var quantity: Int?=null,
    var variantId: String?=""
)