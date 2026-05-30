package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemLanguageBinding
import java.util.Locale

class PanditLanguageAdapter(
    private val panditLanguageList: ArrayList<String>,
    private val onSelected: (String) -> Unit,
) : BaseRecyclerAdapter<ListItemLanguageBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_language

    override fun getItemCount(): Int {
        return panditLanguageList.size
    }

    private var selectedLanguage: String = ""

    override fun bind(binding: ListItemLanguageBinding, position: Int) {
        binding.apply {
            val language = panditLanguageList[position]
            val isSelected = language == selectedLanguage

            val drawableRes = if (isSelected) {
                R.drawable.selected_round_btn
            } else {
                R.drawable.unselected_rounded_btn
            }
            tvLanguage.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)
            tvLanguage.text = language.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }

            clLanguage.setOnClickListener {
                onSelected(language)
            }
        }
    }

    fun selectionChange(language: String) {
        selectedLanguage = language
        notifyDataSetChanged()
    }
}

