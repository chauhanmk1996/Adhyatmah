package com.app.adhyatmah.presentation.ui.pandit_ji.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.RecyclerMultiplePoojaBinding
import java.util.Locale

class SelectBookingLanguageAdapter(
    private val languages: List<String>,
    private val selectedLanguages: MutableList<String>
) : RecyclerView.Adapter<SelectBookingLanguageAdapter.LanguageViewHolder>() {

    inner class LanguageViewHolder(val binding: RecyclerMultiplePoojaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = RecyclerMultiplePoojaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        val isSelected = selectedLanguages.contains(language)

        holder.binding.textView.text = language.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }

        val drawableRes = if (isSelected)
            R.drawable.selected_round_btn
        else
            R.drawable.unselected_rounded_btn

        holder.binding.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)

        holder.binding.textView.setOnClickListener {
            if (isSelected) {
                selectedLanguages.remove(language)
            } else {
                selectedLanguages.add(language)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = languages.size
}

