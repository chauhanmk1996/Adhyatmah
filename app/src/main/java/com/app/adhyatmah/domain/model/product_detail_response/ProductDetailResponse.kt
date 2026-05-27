package com.app.adhyatmah.domain.model.product_detail_response

data class ProductDetailResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)