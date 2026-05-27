package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class Cost(
    val subtotalAmount: SubtotalAmount?=null,
    val totalAmount: TotalAmount?=null,
//    val totalDutyAmount: Any,
    val totalTaxAmount: TotalTaxAmount?=null
)