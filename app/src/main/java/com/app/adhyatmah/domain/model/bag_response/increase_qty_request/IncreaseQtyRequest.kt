package com.app.adhyatmah.domain.model.bag_response.increase_qty_request

data class IncreaseQtyRequest(
    var accessToken: String?="",
    var variant: Variant?=null
)

{
    data class Variant(
        var id: String?="",
        var quantity: Int?=null
    )
}