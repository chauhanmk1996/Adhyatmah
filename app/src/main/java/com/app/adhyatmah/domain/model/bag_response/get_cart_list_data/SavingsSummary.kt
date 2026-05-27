package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class SavingsSummary(
    val currencyCode: String?=null,
    val finalTotal: Double?=null,
    val originalTotal: Int?=null,
    val totalSavings: Double?=null
)