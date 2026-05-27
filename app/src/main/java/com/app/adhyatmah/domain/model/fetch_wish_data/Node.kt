package com.app.adhyatmah.domain.model.fetch_wish_data

data class Node(
    val id: String,
    val price: Price,
    val selectedOptions: List<SelectedOption>,
    val title: String
)