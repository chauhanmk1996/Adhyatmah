package com.app.adhyatmah.domain.model.view_all_product.response

data class Collection(
    val appliedFilters: Any,
    val appliedSorting: Any,
    val description: String,
    val handle: String,
    val id: String,
    val image: Image,
    val pagination: Pagination,
    val products: List<Product>,
    val title: String
)