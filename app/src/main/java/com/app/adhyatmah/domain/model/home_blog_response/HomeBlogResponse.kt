package com.app.adhyatmah.domain.model.home_blog_response

data class HomeBlogResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)