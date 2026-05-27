package com.app.adhyatmah.domain.model.view_all_product.response

data class Product(
    val availableForSale: Boolean,
    val description: String,
    val featuredImage: FeaturedImage,
    val handle: String,
    val id: String,
    val productType: String,
    val tags: List<Any>,
    val title: String,
    val variants: List<Variant>,
    val wishlist: Boolean
)