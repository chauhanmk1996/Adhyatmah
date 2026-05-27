package com.app.adhyatmah.domain.model.home_menu_response

data class Item(
    val collectionHandle: String,
    val collectionId: String,
    val items: List<ItemX>,
    val title: String,
    val type: String,
    val url: String
)