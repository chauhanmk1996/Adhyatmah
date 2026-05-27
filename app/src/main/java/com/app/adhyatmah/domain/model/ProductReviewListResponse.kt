package com.app.adhyatmah.domain.model

data class ProductReviewListResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)

{

    data class Payload(
        val product: Product
    )


    data class Product(
        val handle: String,
        val id: String,
        val pagination: Pagination,
        val reviews: List<Review>,
        val title: String
    )

    data class Review(
        val body: String,
        val created_at: String,
        val curated: String,
        val featured: Boolean,
        val has_published_pictures: Boolean,
        val has_published_videos: Boolean,
        val hidden: Boolean,
        val id: Int,
        val ip_address: Any,
        val pictures: List<Any>,
        val pinned: Boolean,
        val product_external_id: Long,
        val product_handle: String,
        val product_title: String,
        val published: Boolean,
        val rating: Int,
        val reviewer: Reviewer,
        val source: String,
        val title: String,
        val updated_at: String,
        val verified: String
    )

    data class Reviewer(
        val accepts_marketing: Boolean,
        val email: String,
        val external_id: Long,
        val id: Int,
        val name: String,
        val phone: Any,
        val tags: Any,
        val unsubscribed_at: Any
    )

    data class Pagination(
        val current_page: Int,
        val per_page: Int,
        val total: Int,
        val total_pages: Int
    )


}