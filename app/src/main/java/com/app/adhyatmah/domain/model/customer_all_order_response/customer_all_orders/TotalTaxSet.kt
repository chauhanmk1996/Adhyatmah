package com.app.adhyatmah.domain.model.customer_all_order_response.customer_all_orders

data class TotalTaxSet(
    val presentment_money: PresentmentMoney,
    val shop_money: ShopMoney
)