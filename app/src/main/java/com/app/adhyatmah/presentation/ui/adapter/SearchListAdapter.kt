package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.R
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.SearchListItemContainerBinding
import com.app.adhyatmah.domain.model.search_list_response.search_list_api_response.Result

class SearchListAdapter(
    private val items: MutableList<Result>,
    private val searchItemClick: OnItemClickListener,
) : RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>() {

    fun updateItems(newItems: List<Result>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class SearchViewHolder(
        val binding: SearchListItemContainerBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            SearchListItemContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]
        holder.binding.label.text = item.title

        if (items.isNotEmpty()) {
            val priceText = item.priceRange.minVariantPrice.let {
                "${it.currencyCode} ${it.amount}"
            }
            holder.binding.price.text = priceText
            val imageUrl = item.featuredImage.url
            Glide.with(holder.itemView.context).load(imageUrl).into(holder.binding.img)

            holder.binding.root.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    if (item.id.isEmpty()) {
                        Toast.makeText(
                            holder.itemView.context,
                            context.getString(R.string.invalid_item_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        searchItemClick.onItemClick(item)
                    }
                }
            }
        }
    }

    override fun getItemCount() = items.size

    interface OnItemClickListener {
        fun onItemClick(productId: Result)
    }
}