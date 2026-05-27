package com.app.adhyatmah.presentation.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ProductSleeveDetailsRecyBinding

class ProductDetailSleeveAdapter(
        var sleeveList: MutableList<String>
    ) :RecyclerView.Adapter<ProductDetailSleeveAdapter.Viewholder>() {
        inner class Viewholder(var binding: ProductSleeveDetailsRecyBinding):RecyclerView.ViewHolder(binding.root)

    private var enabledSet: Set<String> = emptySet()

    private var selectedPosition = 0
        var onItemClick: ((String) -> Unit)? = null


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ProductDetailSleeveAdapter.Viewholder {
            var binding = ProductSleeveDetailsRecyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return  Viewholder(binding)
        }

        override fun onBindViewHolder(holder: ProductDetailSleeveAdapter.Viewholder, @SuppressLint("RecyclerView") position: Int) {
            var sleeve = sleeveList[position]
            holder.binding.text.text = sleeve


            /*val isEnabled = enabledSet.contains(sleeve)
            holder.binding.text.isEnabled = isEnabled
            holder.binding.text.alpha = if (isEnabled) 1.0f else 0.4f
*/

            if(position == selectedPosition){
                holder.binding.text.setBackgroundResource(R.drawable.rectangle_000000_radius_12)
                holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.white))

            }else{
                holder.binding.text.setTextColor(holder.itemView.context.getColor(R.color.grey_7C7C7C))
                holder.binding.text.setBackgroundResource(R.drawable.rectangle_default_bg) // ✅ replace with your actual default drawable
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