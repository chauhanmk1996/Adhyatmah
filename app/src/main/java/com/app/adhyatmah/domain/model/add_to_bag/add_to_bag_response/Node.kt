package com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_response

data class Node(
    val id: String,
    val merchandise: Merchandise,
    val quantity: Int
)