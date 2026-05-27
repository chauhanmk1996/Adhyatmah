package com.app.adhyatmah.domain.model.faq

data class FAQResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: List<Payload>,
    val status: Int
)