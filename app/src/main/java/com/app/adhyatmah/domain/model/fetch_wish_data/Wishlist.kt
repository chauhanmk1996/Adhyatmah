package com.app.adhyatmah.domain.model.fetch_wish_data

data class Wishlist(
    val description: String,
    val featuredImage: FeaturedImage,
    val handle: String,
    val id: String,
    val title: String,
    val variants: Variants,
    val wishlist: Boolean
)