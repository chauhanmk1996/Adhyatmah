package com.app.adhyatmah.domain.model.customer_all_order_response.cancel_order_response

data class DiscountedPriceSet(
    val presentment_money: PresentmentMoney,
    val shop_money: ShopMoney
)