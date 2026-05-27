package com.app.adhyatmah.presentation.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ProductDetailSizeRecyclerBinding

class ProductDetailSizeAdapter(
    var sizeList: MutableList<String>
) :RecyclerView.Adapter<ProductDetailSizeAdapter.Viewholder>() {
    inner class Viewholder(var binding: ProductDetailSizeRecyclerBinding):RecyclerView.ViewHolder(binding.root)
       var isTrue:Boolean= false

    private var enabledSet: Set<String> = emptySet()
    private var selectedPosition = 0
    var onItemClick: ((String) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductDetailSizeAdapter.Viewholder {
        var binding = ProductDetailSizeRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  Viewholder(binding)



    }

    override fun onBindViewHolder(holder: ProductDetailSizeAdapter.Viewholder, @SuppressLint("RecyclerView") position: Int) {
            val size = sizeList[position]
            holder.binding.text.text = size

            val isEnabled = enabledSet.contains(size)
            holder.binding.text.isEnabled = isEnabled
            holder.binding.text.alpha = if (isEnabled) 1.0f else 0.4f

            if (position == selectedPosition && isEnabled) {
                holder.binding.text.setBackgroundResource(R.drawable.rectangle_000000_radius_12)
                holder.binding.text.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            } else {
                holder.binding.text.setBackgroundResource(R.drawable.rectangle_default_bg)
                holder.binding.text.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.grey_7C7C7C))
            }

            holder.binding.text.setOnClickListener {
                if (isEnabled) {
                    val prev = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(prev)
                    notifyItemChanged(selectedPosition)
                    onItemClick?.invoke(size)
                }
            }

        }

    fun updateSizeList(allSizes: List<String>, enabledSizes: List<String>) {
        sizeList.clear()
        sizeList.addAll(allSizes)
        enabledSet = enabledSizes.toSet()  // Save which sizes are valid
        notifyDataSetChanged()
    }


/*
    fun updateSizeList(newList: List<String>) {
        sizeList.clear()
        sizeList.addAll(newList)
        notifyDataSetChanged()
    }
*/

    override fun getItemCount(): Int {
        return sizeList.size
    }
}