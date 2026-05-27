package com.app.adhyatmah.domain.model.home_menu_response

data class Menu(
    val handle: String,
    val id: String,
    val items: List<Item>,
    val title: String
)