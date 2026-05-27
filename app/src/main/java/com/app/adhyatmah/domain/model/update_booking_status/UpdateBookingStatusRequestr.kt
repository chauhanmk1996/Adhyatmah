package com.app.adhyatmah.domain.model.update_booking_status

data class UpdateBookingStatusRequest(
    var bookingId: String?=null,
    var status: String?=null
)
