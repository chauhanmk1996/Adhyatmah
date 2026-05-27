package com.app.adhyatmah.domain.model.shipping_response

data class Payload(
    val shoppingUrls: List<ShoppingUrl>,
    val states: List<String>
)