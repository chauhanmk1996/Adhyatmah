package com.app.adhyatmah.domain.model.home_blog_response

data class Blog(
    val articles: Articles,
    val handle: String,
    val id: String,
    val title: String
)