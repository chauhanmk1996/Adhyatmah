package com.app.adhyatmah.domain.model.customer_all_order_response.customer_order_details

data class SubtotalPriceSet(
    val presentment_money: PresentmentMoney,
    val shop_money: ShopMoney
)