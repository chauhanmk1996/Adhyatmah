package com.app.adhyatmah.domain.model.pandit_list.get_pandit_list

data class Vendor(
    val views:Int?,
    val about: String?=null,
    val address: String?=null,
    val city: String?=null,
    val email: String?=null,
    val experience: String?=null,
    val firstName: String?=null,
    val id: String?=null,
    val image: Image?=null,
    val language: List<String>?=null,
    val lastName: String?=null,
    val phone: String?=null,
    val services: List<Service>?=null
)

data class Image(
    val _id: String?=null,
    val url: String?=null,
)
data class Service(
    val id: String?=null,
    val poojaType: String?=null,
    val description: String?=null,
    val duration: String?=null,
    val price: Int?=null,
)