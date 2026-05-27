package com.app.adhyatmah.domain.model.reviews


data class Review(
    val name: String,
    val comment: String,
    val daysAgo: String,
    val ratingIconResId: Int,
    val profilePicResId: Int
)
