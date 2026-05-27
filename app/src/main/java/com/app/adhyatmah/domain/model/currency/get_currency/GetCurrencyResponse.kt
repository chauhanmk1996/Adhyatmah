package com.app.adhyatmah.domain.model.currency.get_currency

data class GetCurrencyResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)