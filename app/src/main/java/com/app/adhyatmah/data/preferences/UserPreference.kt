package com.app.adhyatmah.data.preferences

import com.app.adhyatmah.domain.model.create_booking.*

object UserPreference {
    var panditjiBookingRequest = PanditjiBookingRequest()
    var CART_COUNT = 0
    var MOBILE_NUMBER = "MOBILE_NUMBER"
    var panditJiDetails = PanditJiDetails()

    var address1 = ""
    var address2 = ""
    var city = ""
    var province = ""
    var country = ""
    var zip = ""

    var savedAddressId = ""
    var selectedType:String? = null
    var search:String? = null
}