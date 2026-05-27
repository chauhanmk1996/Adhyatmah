package com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_response

data class Cart(
    val checkoutUrl: String,
    val id: String,
    val lines: Lines
)