package com.app.adhyatmah.domain.model.home_collection_Response

data class HomeCollectionResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val status: Int,
    val payload: HomeCollectionPayload?,
)

data class HomeCollectionPayload(
    val collections: ArrayList<HomeCollection>?,
    val cart: Int? = null,
)

data class HomeCollection(
    val id: String,
    val title: String?,
    val description: String?,
    val handle: String?,
    val image: Image?,
    val viewAllUrl: String?,
    val products: ArrayList<Product>?,
)

data class Product(
    val id: String,
    val title: String?,
    val featuredImage: FeaturedImage,
    val handle: String?,
    var wishlist: Boolean?,
    val variant: Variant?,
    val stockQuantity: Int,
    val pincode : List<String> ?= null
)

data class FeaturedImage(
    val url: String?,
)

data class Image(
    val url: String?
)

data class Variant(
    val id: String,
    val price: Price?,
    val selectedOptions: ArrayList<SelectedOption>?
)

data class SelectedOption(
    val name: String?,
    val value: String?
)

data class Price(
    val amount: String?,
    val currencyCode: String?
)