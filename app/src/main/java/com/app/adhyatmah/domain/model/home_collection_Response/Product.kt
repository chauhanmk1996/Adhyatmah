package com.app.adhyatmah.domain.model.home_collection_Response

data class Product(
    val featuredImage: FeaturedImage,
    val handle: String,
    val id: String,
    val title: String,
    val variant: Variant,
    var wishlist: Boolean
)