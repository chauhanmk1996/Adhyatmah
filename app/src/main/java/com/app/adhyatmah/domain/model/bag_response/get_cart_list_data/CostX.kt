package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class CostX(
    val amountPerQuantity: AmountPerQuantity,
    val totalAmount: TotalAmount?=null
)