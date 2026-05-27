package com.app.adhyatmah.domain.model.wish_list.wish_list_response

data class Product(
    val description: String,
    val featuredImage: FeaturedImage,
    val handle: String,
    val id: String,
    val title: String,
    val variants: Variants,
    val wishlist: Boolean
)