package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class DiscountInfo(
    val appliedDiscounts: List<AppliedDiscount>?=null,
    val hasDiscounts: Boolean?=null,
    val savingsSummary: SavingsSummary?=null,
    val totalDiscount: Double?=null
)