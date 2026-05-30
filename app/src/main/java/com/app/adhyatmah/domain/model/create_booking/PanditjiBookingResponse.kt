package com.app.adhyatmah.domain.model.create_booking

data class PanditJiBookingResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int,
)

data class Payload(
    val booking: Booking,
)

data class Booking(
    val id: String
)

data class Address(
    val city: String,
    val country: String,
    val state: String,
    val streetAddress: String,
    val zip: String,
)

data class Customer(
    val email: String,
    val firstName: String,
    val id: String,
    val lastName: String,
)

data class Service(
    val id: String,
    val poojaType: String,
)

data class Vendor(
    val firstName: String,
    val id: String,
    val lastName: String,
)