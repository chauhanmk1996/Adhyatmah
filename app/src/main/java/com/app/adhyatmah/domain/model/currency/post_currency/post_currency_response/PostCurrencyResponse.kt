package com.app.adhyatmah.domain.model.currency.post_currency.post_currency_response

data class PostCurrencyResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)