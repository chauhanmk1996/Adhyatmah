package com.app.adhyatmah.data.local

data class IntroSlideData( val title: String,
                           val description: String,
                           val imageResId: Int,
                           var isSelected:Boolean = false)