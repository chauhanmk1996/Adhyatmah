package com.app.adhyatmah.domain.model.bag_response.get_cart_list_data

data class Node(
    val cost: CostX?=null,
//    val discountAllocations: List<Any?>,
    val id: String?=null,
    val merchandise: Merchandise?=null,
    var quantity: Int?=null
)