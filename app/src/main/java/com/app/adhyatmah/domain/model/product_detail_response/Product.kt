package com.app.adhyatmah.domain.model.product_detail_response

data class Product(
    val description: String,
    val handle: String,
    val stockQuantity: Int,
    val id: String,
    val images: MutableList<Image>,
    val options: List<Option>,
    val title: String,
    val variants: List<Variant>?=null,
    val wishlist: Boolean,
    val pincode : List<String> ?= null
)