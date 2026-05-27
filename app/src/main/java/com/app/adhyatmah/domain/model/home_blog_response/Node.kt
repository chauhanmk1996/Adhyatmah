package com.app.adhyatmah.domain.model.home_blog_response

data class Node(
    val content: String,
    val excerpt: String,
    val handle: String,
    val id: String,
    val image: Image,
    val publishedAt: String,
    val title: String
)