package com.app.adhyatmah.domain.model.view_all_product.request

data class ViewAllProductRequest(
    val handle: String? = null,
    val sortBy: String? = null,
    val filters: Filters?= null
) {
    data class Filters(
        val color: List<String>? = null,
        val size: List<String>? = null,
        val brand: String? = null,
        val minPrice: Int? = null,
        val maxPrice: Int? = null
    )
}
