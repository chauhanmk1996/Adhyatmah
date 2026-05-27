package com.app.adhyatmah.domain.model.view_all_product.response

data class ViewAllProductResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)