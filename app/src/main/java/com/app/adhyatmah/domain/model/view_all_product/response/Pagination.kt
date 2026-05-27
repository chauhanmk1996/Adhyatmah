package com.app.adhyatmah.domain.model.view_all_product.response

data class Pagination(
    val currentPage: Int,
    val limitPerPage: Int,
    val totalPages: Int,
    val totalProducts: Int
)