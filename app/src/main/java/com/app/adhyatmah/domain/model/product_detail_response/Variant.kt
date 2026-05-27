package com.app.adhyatmah.domain.model.product_detail_response

data class Variant(
    val availableForSale: Boolean,
    var id: String?="",
    val price: Price?=null,
    val selectedOptions: List<SelectedOption>,
    val title: String
)