package com.app.adhyatmah.domain.model.filter.getFilter

data class Key(
    val brand: MutableList<String>,
    val color: MutableList<String>,
    val gender: MutableList<String>,
    val maxPrice: Int,
    val minPrice: Int,
    val size: MutableList<String>
)