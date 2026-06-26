package com.app.adhyatmah.domain.model

data class AllCategoryListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)

{

    data class Payload(
        val collections: List<Collection>,
        val pagination: Pagination
    )

    data class FeaturedImage(
        val url: String
    )
    data class Collection(
        val description: String,
        val handle: String,
        val id: String,
        val image: Image,
        val products: List<Product>,
        val title: String,
        val viewAllUrl: String,
        val product_count:Int?
    )

    data class Image(
        val altText: Any,
        val url: String
    )



    data class Price(
        val amount: String,
        val currencyCode: String
    )

    data class Product(
        val featuredImage: FeaturedImage,
        val handle: String,
        val id: String,
        val title: String,
        val variant: Variant
    )

    data class SelectedOption(
        val name: String,
        val value: String
    )


    data class Variant(
        val id: String,
        val price: Price,
        val selectedOptions: List<SelectedOption>
    )

    data class Pagination(
        val currentPage: Int,
        val perPage: Int,
        val total: Int,
        val totalPages: Int
    )

}