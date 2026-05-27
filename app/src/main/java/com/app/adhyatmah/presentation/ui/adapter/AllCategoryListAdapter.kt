package com.app.adhyatmah.presentation.ui.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.CategoryItemContainerBinding
import com.app.adhyatmah.databinding.ItemLeftCategoryBinding
import com.app.adhyatmah.domain.model.AllCategoryListResponse


/*
class AllCategoryListAdapter(
    private val items: MutableList<AllCategoryListResponse.Collection>,
    private val onItemClick: (AllCategoryListResponse.Collection) -> Unit

) : RecyclerView.Adapter<AllCategoryListAdapter.CategoryViewHolder>() {

    private val originalList = mutableListOf<AllCategoryListResponse.Collection>()


    fun updateItems(newItems: List<AllCategoryListResponse.Collection>) {
       */
/* items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()*//*


        originalList.clear()
        originalList.addAll(newItems)

        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
    fun addItems(newItems: List<AllCategoryListResponse.Collection>) {
        val start = items.size
        items.addAll(newItems)
        originalList.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }


    inner class CategoryViewHolder(val binding: CategoryItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            CategoryItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = items[position]
        holder.binding.label.text = item.title

        Glide.with(holder.itemView.context)
            .load(item.image.url)
            .into(holder.binding.img)


        holder.binding.root.setOnClickListener {

            val adptrposition = position
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(item)
            }
            Log.d("tfd","Data:${adptrposition}")

        }

//      Log.d("TAG", "onBindViewHolder: ${item.discription}")
    }

    override fun getItemCount() = items.size

    interface OnItemClickListener {
        fun onItemClick(item: AllCategoryListResponse.Collection)
    }

    fun filter(query: String) {
        val lowercaseQuery = query.lowercase()
        items.clear()
        if (lowercaseQuery.isEmpty()) {
            items.addAll(originalList)
        } else {
            items.addAll(
                originalList.filter {
                    it.title.contains(lowercaseQuery, ignoreCase = true)
                }
            )
        }
        notifyDataSetChanged()
    }

}*/


class AllCategoryListAdapter(
    private val items: MutableList<AllCategoryListResponse.Collection>,
    private val onItemClick: (AllCategoryListResponse.Collection) -> Unit
) : RecyclerView.Adapter<AllCategoryListAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0
    private val originalList = mutableListOf<AllCategoryListResponse.Collection>()

    fun updateItems(newItems: List<AllCategoryListResponse.Collection>) {
        originalList.clear()
        originalList.addAll(newItems)

        items.clear()
        items.addAll(newItems)

        selectedPosition = 0
        Log.i("TAG", "updateItems: "+newItems)
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(val binding: ItemLeftCategoryBinding)
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

        // ---- SELECT / UNSELECT UI ----
        if (position == selectedPosition) {
            holder.binding.root.setBackgroundColor(Color.parseColor("#EFEFEF"))
            holder.binding.label.setTextColor(Color.parseColor("#000000"))
        } else {
            holder.binding.root.setBackgroundColor(Color.WHITE)
            holder.binding.label.setTextColor(Color.parseColor("#666666"))
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
    fun setSelectedCategory(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

}

