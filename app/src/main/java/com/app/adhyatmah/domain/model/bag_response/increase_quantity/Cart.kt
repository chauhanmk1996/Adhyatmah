package com.app.adhyatmah.domain.model.bag_response.increase_quantity

data class Cart(
    val checkoutUrl: String,
    val id: String,
    val lines: Lines
)