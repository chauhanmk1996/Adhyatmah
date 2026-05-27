package com.app.adhyatmah.domain.model.currency.get_currency

data class Currency(
    val countryCode: String,
    val currencyCode: String,
    val name: String,
    val symbol: String
)