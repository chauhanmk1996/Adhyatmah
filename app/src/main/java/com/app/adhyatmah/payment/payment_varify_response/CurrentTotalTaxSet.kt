package com.app.adhyatmah.payment.payment_varify_response

data class CurrentTotalTaxSet(
    val presentment_money: PresentmentMoney,
    val shop_money: ShopMoney
)