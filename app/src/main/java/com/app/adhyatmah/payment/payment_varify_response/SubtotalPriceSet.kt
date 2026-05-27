package com.app.adhyatmah.payment.payment_varify_response

data class SubtotalPriceSet(
    val presentment_money: PresentmentMoney,
    val shop_money: ShopMoney
)