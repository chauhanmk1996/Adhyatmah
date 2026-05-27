package com.app.adhyatmah.presentation.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.SearchListItemContainerBinding
import com.app.adhyatmah.domain.model.search_list_response.search_list_api_response.Result

class SearchListAdapter(
    private val items: MutableList<Result>,
    private val searchItemClick: OnItemClickListener

    ) : RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>() {

        fun updateItems(newItems: List<Result>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }


        inner class SearchViewHolder(
            val binding: SearchListItemContainerBinding ) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
            val binding =
                SearchListItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchViewHolder(binding)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            val item = items[position]
            holder.binding.label.text =item.title

            if(items.isNullOrEmpty()){

            }else{
                val priceText = item.priceRange?.minVariantPrice?.let {
                    "${it.currencyCode} ${it.amount}"
                } ?: "N/A" // Fallback if null
                holder.binding.price.text= priceText  //item.priceRange.minVariantPrice.currencyCode+" "+ priceText/*item.priceRange.minVariantPrice.amount*/
                val imageUrl = item.featuredImage?.url
                if (imageUrl != null) {
                    Glide.with(holder.itemView.context).load(imageUrl).into(holder.binding.img)
                } else {
                    holder.binding.cardView.visibility = View.VISIBLE
                 //   holder.binding.img.setImageResource(R.drawable.edit_profile_img) // ← fallback image
                }
                holder.binding.root.setOnClickListener {
                    val adptrposition = position
                    if (position != RecyclerView.NO_POSITION) {
                        if(item.id.isNullOrEmpty()){
                            Toast.makeText(holder.itemView.context, "Invalid item. Please try again.", Toast.LENGTH_SHORT).show()
                        }else{
                            searchItemClick.onItemClick(item)
                        }
                    }
                    Log.d("tfd","Data:${adptrposition}")

                }

            }

        }

        override fun getItemCount() = items.size

        interface OnItemClickListener {
            fun onItemClick(productId: Result)
        }


}