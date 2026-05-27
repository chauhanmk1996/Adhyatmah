package com.app.adhyatmah.domain.model.home_collection_Response

data class Collection(
    val description: String,
    val handle: String,
    val id: String,
    val image: Image,
    val products: List<Product>,
    val title: String,
    val viewAllUrl: String
)