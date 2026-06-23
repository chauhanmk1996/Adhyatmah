package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.DataString
import com.app.adhyatmah.databinding.ColorsListLayoutBinding

class ColorsListAdapter(
    private val colorsList: MutableList<DataString>,
    private val onColorClick: (List<DataString>) -> Unit,
) : RecyclerView.Adapter<ColorsListAdapter.ColorViewHolder>() {
    private val selectedColors = mutableListOf<DataString>()

    inner class ColorViewHolder(private val binding: ColorsListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(colorItem: DataString) {
            binding.colorsTv.text = colorItem.title

            binding.colorsTv.setBackgroundResource(
                if (colorItem.isSelect) {
                    R.drawable.rectangle_black
                } else R.drawable.rectangle_filter
            )
            val context = binding.root.context

            binding.colorsTv.setTextColor(
                if (colorItem.isSelect) ContextCompat.getColor(
                    context,
                    R.color.white
                ) else ContextCompat.getColor(context, R.color.black)
            )

            binding.root.setOnClickListener {
                colorItem.isSelect = !colorItem.isSelect
                if (colorItem.isSelect) {
                    selectedColors.add(colorItem)
                } else {
                    selectedColors.removeAll { it.title == colorItem.title }
                }
                onColorClick(selectedColors)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ColorsListLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colorsList[position])
    }

    override fun getItemCount(): Int = colorsList.size
}