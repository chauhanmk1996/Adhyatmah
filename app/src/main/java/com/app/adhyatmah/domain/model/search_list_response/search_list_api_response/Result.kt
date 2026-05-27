package com.app.adhyatmah.domain.model.search_list_response.search_list_api_response

data class Result(
    val __typename: String,
    val description: String,
    val featuredImage: FeaturedImage,
    val handle: String,
    val id: String,
    val priceRange: PriceRange,
    val title: String
)