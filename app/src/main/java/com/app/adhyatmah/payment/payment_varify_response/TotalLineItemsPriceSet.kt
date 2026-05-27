package com.app.adhyatmah.payment.payment_varify_response

data class TotalLineItemsPriceSet(
    val presentment_money: PresentmentMoney,
    val shop_money: ShopMoney
)