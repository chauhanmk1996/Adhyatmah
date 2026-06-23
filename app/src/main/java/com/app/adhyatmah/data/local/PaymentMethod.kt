package com.app.adhyatmah.data.local

data class PaymentMethod(
    var id: String? = "",
    var name: String? = "",
    var type: String? = "",
    var icon: String? = "",
    var isSelected: Boolean = false,
)