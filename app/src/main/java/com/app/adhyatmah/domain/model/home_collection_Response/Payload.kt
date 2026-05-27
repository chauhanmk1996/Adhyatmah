package com.app.adhyatmah.domain.model.home_collection_Response

data class Payload(
    val collections: List<Collection>,
    val cart: Int?=null
)