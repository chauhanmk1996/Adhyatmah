package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ProductSleeveDetailsRecyBinding

class ProductDetailSleeveAdapter(
    var sleeveList: MutableList<String>,
) : RecyclerView.Adapter<ProductDetailSleeveAdapter.ViewHolder>() {

    class ViewHolder(var binding: ProductSleeveDetailsRecyBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var enabledSet: Set<String> = emptySet()
    private var selectedPosition = 0
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ProductSleeveDetailsRecyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sleeve = sleeveList[position]
        holder.binding.text.text = sleeve

        if (position == selectedPosition) {
            holder.binding.text.setBackgroundResource(R.drawable.rectangle_000000_radius_12)
            holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.white))

        } else {
            holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.grey_7C7C7C))
            holder.binding.text.setBackgroundResource(R.drawable.rectangle_default_bg)
        }

        holder.binding.text.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(sleeve)
        }
    }

    fun updateList(newList: List<String>, distinct: List<String>) {
        sleeveList.clear()
        sleeveList.addAll(newList)
        enabledSet = distinct.toSet()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return sleeveList.size
    }
}