package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemLanguageBinding
import java.util.Locale

class LanguageAdapter(
    private val languagesList: List<String>,
    private val selectedLanguages: MutableList<String>,
) : BaseRecyclerAdapter<ListItemLanguageBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_language

    override fun bind(binding: ListItemLanguageBinding, position: Int) {
        val language = languagesList[position]
        val isSelected = selectedLanguages.contains(language)

        binding.apply {
            tvLanguage.text = language.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }

            val drawableRes = if (isSelected) {
                R.drawable.selected_round_btn
            } else {
                R.drawable.unselected_rounded_btn
            }

            tvLanguage.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)

            tvLanguage.setOnClickListener {
                if (isSelected) {
                    selectedLanguages.remove(language)
                } else {
                    selectedLanguages.add(language)
                }
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return languagesList.size
    }
}

