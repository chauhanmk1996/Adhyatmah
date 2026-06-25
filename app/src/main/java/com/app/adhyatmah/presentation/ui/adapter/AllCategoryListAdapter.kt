package com.app.adhyatmah.presentation.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.ItemLeftCategoryBinding
import com.app.adhyatmah.domain.model.AllCategoryListResponse
import androidx.core.graphics.toColorInt

class AllCategoryListAdapter(
    private val items: MutableList<AllCategoryListResponse.Collection>,
    private val onItemClick: (AllCategoryListResponse.Collection) -> Unit,
) : RecyclerView.Adapter<AllCategoryListAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0
    private val originalList = mutableListOf<AllCategoryListResponse.Collection>()

    class CategoryViewHolder(val binding: ItemLeftCategoryBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemLeftCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position]
        val ctx = holder.itemView.context

        holder.binding.label.text = item.title

        Glide.with(ctx)
            .load(item.image.url)
            .into(holder.binding.img)

        if (position == selectedPosition) {
            holder.binding.root.setBackgroundColor("#EFEFEF".toColorInt())
            holder.binding.label.setTextColor("#000000".toColorInt())
        } else {
            holder.binding.root.setBackgroundColor(Color.WHITE)
            holder.binding.label.setTextColor("#666666".toColorInt())
        }

        holder.binding.root.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                val previous = selectedPosition
                selectedPosition = position
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onItemClick(item)
            }
        }
    }

    override fun getItemCount() = items.size

    fun filter(query: String) {
        val q = query.lowercase()
        items.clear()
        if (q.isEmpty()) {
            items.addAll(originalList)
        } else {
            items.addAll(originalList.filter {
                it.title.contains(q, ignoreCase = true)
            })
        }
        notifyDataSetChanged()
    }

    fun updateItems(newItems: List<AllCategoryListResponse.Collection>) {
        originalList.clear()
        originalList.addAll(newItems)
        items.clear()
        items.addAll(newItems)
        selectedPosition = 0
        notifyDataSetChanged()
    }

    fun setSelectedCategory(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}