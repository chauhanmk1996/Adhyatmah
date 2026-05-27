package com.app.adhyatmah.domain.model.home_banner_response

data class Banners(
    var homeBanners:MutableList<HomeBanner> ,
    val subBanners: List<SubBanner>
)