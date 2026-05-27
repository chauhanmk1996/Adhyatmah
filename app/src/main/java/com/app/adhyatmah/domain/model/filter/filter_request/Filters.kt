package com.app.adhyatmah.domain.model.filter.filter_request

data class Filters(
    val color: List<String>?=null,
    val size: List<String>?=null,
    val gender: List<String>?=null,
    val minPrice:Int?=null,
    val maxPrice:Int?=null
)