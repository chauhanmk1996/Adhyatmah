package com.app.adhyatmah.domain.model

data class HomeResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val status: Int,
    val payload: PopularPoojaPayload?,
)

data class PopularPoojaPayload(
    val services: ArrayList<PopularPooja>?,
    val longBanner: LongBanner?,
    val whyChooseUs: ArrayList<WhyChooseUs>?,
    val testimonialsData: TestimonialsData?,
)

data class PopularPooja(
    val id: String,
    val views: Int?,
    val image: Image?,
    val name: String?,
    val duration: String?,
    val price: Double?,
    val originalPrice: Double?,
)

data class LongBanner(
    val id: String,
    val url: String?,
    val title: String?,
    val subtitle: String?,
)

data class WhyChooseUs(
    val id: String,
    val url: String?,
    val title: String?,
)

data class TestimonialsData(
    val title: String?,
    val rating: String?,
    val totalReviews: String?,
    val testimonials: ArrayList<Testimonials>?,
)

data class Testimonials(
    val id: String,
    val name: String?,
    val stars: String?,
    val daysAgo: String?,
    val service: String?,
    val review: String?,
)

data class PopularPujaResponse(
    val data: ArrayList<PopularPooja>?,
)