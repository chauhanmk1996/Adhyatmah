package com.app.adhyatmah.data.preferences

import com.app.adhyatmah.domain.model.create_booking.*

object UserPreference {
    var panditjiBookingRequest = PanditjiBookingRequest()
    var CART_COUNT = 0
    var MOBILE_NUMBER = "MOBILE_NUMBER"
    var panditJiDetails = PanditJiDetails()
    var savedAddress = ""
    var savedAddressId = ""
    var selectedType:String? = null
    var search:String? = null
}