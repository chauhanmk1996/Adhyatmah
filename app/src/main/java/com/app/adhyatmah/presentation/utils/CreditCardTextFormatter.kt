package com.app.adhyatmah.presentation.utils

import android.text.Editable
import android.text.TextWatcher

class CreditCardTextFormatter(private val divider: Char = ' ', private val dividerInterval: Int = 4) :
    TextWatcher {

    private var isFormatting: Boolean = false
    private var deletingHyphen: Boolean = false
    private var hyphenStart: Int = 0
    private var deletingBackward: Boolean = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Check if user is deleting a space
        if (count == 1 && after == 0) {
            val charAtStart = s?.get(start)
            deletingHyphen = charAtStart == divider
            hyphenStart = start
            deletingBackward = true
        } else {
            deletingHyphen = false
            deletingBackward = false
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return

        isFormatting = true

        // Remove all divider characters
        val textWithoutSpaces = s.toString().replace(divider.toString(), "")

        // Format the string
        val formatted = StringBuilder()
        for (i in textWithoutSpaces.indices) {
            formatted.append(textWithoutSpaces[i])
            if ((i + 1) % dividerInterval == 0 && (i + 1) != textWithoutSpaces.length) {
                formatted.append(divider)
            }
        }

        s?.replace(0, s.length, formatted.toString())

        isFormatting = false
    }
}
