package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.DataString
import com.app.adhyatmah.databinding.CategoryListLayoutBinding


class CategoryListAdapter(
    private val categoryList: MutableList<DataString>,
    private val onColorClick: (List<DataString>) -> Unit
): RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {
    private val selectedSize = mutableListOf<DataString>()

    inner class CategoryViewHolder(private val binding: CategoryListLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(colorItem: DataString) {
            binding.colorsTv.text = colorItem.title
            binding.colorsTv.setBackgroundResource(
                if (colorItem.isSelect) R.drawable.rectangle_black else R.drawable.rectangle_filter
            )
            val context = binding.root.context

            binding.colorsTv.setTextColor(
                if (colorItem.isSelect) ContextCompat.getColor(context,R.color.white)else ContextCompat.getColor(context,R.color.black)
            )

            binding.root.setOnClickListener {
                colorItem.isSelect = !colorItem.isSelect
                if (colorItem.isSelect) {
                    selectedSize.add(colorItem)
                } else {
                    selectedSize.removeAll { it.title == colorItem.title }
                }
                notifyDataSetChanged()
                onColorClick(selectedSize)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryListLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.bind(categoryList[position])

    }

    override fun getItemCount(): Int = categoryList.size



}
