package com.app.adhyatmah.domain.model.home_banner_response

data class HomeBannerResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)