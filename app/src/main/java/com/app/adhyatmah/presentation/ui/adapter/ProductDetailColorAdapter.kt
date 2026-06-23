package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ProductDetailColorRecyclerBinding

class ProductDetailColorAdapter(var colorSize: MutableList<String>) :
    RecyclerView.Adapter<ProductDetailColorAdapter.ViewHolder>() {

    private var enabledSet: Set<String> = emptySet()
    private var selectedPosition = 0
    var onItemClick: ((String) -> Unit)? = null

    class ViewHolder(var binding: ProductDetailColorRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun updateColorList(newList: List<String>, distinct: List<String>) {
        colorSize.clear()
        colorSize.addAll(newList)
        enabledSet = distinct.toSet()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductDetailColorRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = colorSize[position]
        holder.binding.text.text = list

        val isEnabled = enabledSet.contains(list)
        holder.binding.text.isEnabled = isEnabled
        holder.binding.text.alpha = if (isEnabled) 1.0f else 0.4f

        if (!isEnabled) {
            holder.binding.text.setBackgroundResource(R.drawable.cross_overlay)
        } else {
            holder.binding.text.setBackgroundResource(R.drawable.rectangle_default_bg)
        }

        if (selectedPosition == position && isEnabled) {
            holder.binding.text.setBackgroundResource(R.drawable.rectangle_000000_radius_12)
            holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.white))

        } else {
            holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.grey_7C7C7C))
            holder.binding.text.setBackgroundResource(R.drawable.rectangle_default_bg)
        }

        holder.binding.text.setOnClickListener {
            val previousClick = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousClick)
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(list)
        }
    }

    override fun getItemCount(): Int {
        return colorSize.size
    }
}