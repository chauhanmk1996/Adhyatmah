package com.app.adhyatmah.domain.model.profile.manage_address

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Addresse(
    val address1: String?="",
    val address2: String?="",
    val city: String?="",
    val country: String?="",
    val id: String?="",
    val name: String?="",
    val phone: String?="",
    val province: String?="",
    val zip: String?=""
): Parcelable