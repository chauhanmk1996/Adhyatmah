package com.app.adhyatmah.domain.model.view_all_product.response

data class Variant(
    val id: String,
    val price: Price,
    val selectedOptions: List<SelectedOption>,
    val title: String
)