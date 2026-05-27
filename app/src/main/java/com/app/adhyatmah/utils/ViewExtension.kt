package com.app.adhyatmah.utils

import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView

fun AppCompatTextView.getString(): String {
    return this.text.trim().toString()
}

fun AppCompatTextView.getLength(): Int {
    return this.text.trim().toString().length
}

fun AppCompatEditText.getString(): String {
    return this.text?.trim()?.toString() ?: ""
}

fun AppCompatEditText.getLength(): Int {
    return this.text?.trim()?.toString()?.length ?: 0
}

fun formatViews(views: Int): String {
    return when {
        views >= 1_000_000 -> String.format("%.1fM", views / 1_000_000f)
        views >= 1_000 -> String.format("%.1fK", views / 1_000f)
        else -> views.toString()
    }
}

fun Double.getDigit(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        java.text.DecimalFormat("#.##").format(this)
    }
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.hide(){
    this.visibility = View.GONE
}