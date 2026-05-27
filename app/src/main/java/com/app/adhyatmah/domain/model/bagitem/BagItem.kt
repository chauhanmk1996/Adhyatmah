package com.app.adhyatmah.domain.model.bagitem


data class BagItem(
    val imageResId: Int,
    val name: String,
    val price: String,
    val size: String,
    val color: String,
    var quantity: Int
)