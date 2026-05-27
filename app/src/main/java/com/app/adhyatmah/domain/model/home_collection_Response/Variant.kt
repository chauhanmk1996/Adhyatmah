package com.app.adhyatmah.domain.model.home_collection_Response

data class Variant(
    val id: String,
    val price: Price,
    val selectedOptions: List<SelectedOption>
)