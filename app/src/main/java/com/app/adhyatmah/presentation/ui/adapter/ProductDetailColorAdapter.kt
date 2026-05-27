package com.app.adhyatmah.presentation.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ProductDetailColorRecyclerBinding

class ProductDetailColorAdapter(var colorSize: MutableList<String>) :RecyclerView.Adapter<ProductDetailColorAdapter.Viewholder>() {

    private var enabledSet: Set<String> = emptySet()
    private var selectedPosition = 0
    var onItemClick: ((String) -> Unit)? = null


    inner class Viewholder(var binding: ProductDetailColorRecyclerBinding):RecyclerView.ViewHolder(binding.root)



    fun updateColorList(newList: List<String>, distinct: List<String>) {
        colorSize.clear()
        colorSize.addAll(newList)
//        selectedPosition = 0
        enabledSet = distinct.toSet()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductDetailColorAdapter.Viewholder {
        val binding = ProductDetailColorRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  Viewholder(binding)
    }

    override fun onBindViewHolder(holder: ProductDetailColorAdapter.Viewholder, @SuppressLint("RecyclerView") position: Int) {
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




        if(selectedPosition==position && isEnabled){
            holder.binding.text.setBackgroundResource(R.drawable.rectangle_000000_radius_12)
            holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.white))

        }else{
            holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.grey_7C7C7C))
            holder.binding.text.setBackgroundResource(R.drawable.rectangle_default_bg) // with your actual default drawable

        }

        holder.binding.text.setOnClickListener {
            var previsousClick = selectedPosition
            selectedPosition = position
            notifyItemChanged(previsousClick)
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(list)

        }
    }

    override fun getItemCount(): Int {
        return colorSize.size
    }
}