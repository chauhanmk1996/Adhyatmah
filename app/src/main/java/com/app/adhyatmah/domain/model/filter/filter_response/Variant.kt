package com.app.adhyatmah.domain.model.filter.filter_response

data class Variant(
    val id: String,
    val price: Price,
    val selectedOptions: List<SelectedOption>,
    val title: String
)