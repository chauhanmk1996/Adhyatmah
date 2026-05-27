package com.app.adhyatmah.domain.model.wish_list.wish_list_response

data class Node(
    val id: String,
    val price: Price,
    val selectedOptions: List<SelectedOption>,
    val title: String
)