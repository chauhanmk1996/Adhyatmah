package com.app.adhyatmah.presentation.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.WishlistItemContainerBinding
import com.app.adhyatmah.domain.model.fetch_wish_data.Wishlist

class WishListAdapter(
    private val items: MutableList<Wishlist>,
    private val onWishlistRemove: (Wishlist, Int) -> Unit,
) : RecyclerView.Adapter<WishListAdapter.WishListViewHolder>() {

    private val selectedItems = mutableListOf<Boolean>()

    fun updateItems(newItems: List<Wishlist>) {
        Log.d("TAG", "updateItems: $newItems")
        items.clear()
        items.addAll(newItems)
        Log.d("TAG", "updateItems: $items")
        selectedItems.clear()
        selectedItems.addAll(List(newItems.size) { true }) // Mark all as selected initially
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        selectedItems.removeAt(position)
        notifyItemRemoved(position)
    }

    class WishListViewHolder(val binding: WishlistItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListViewHolder {
        val binding =
            WishlistItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WishListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishListViewHolder, position: Int) {
        val item = items[position]
        val tt = item.variants.edges
        holder.binding.label.text = item.title
        val featuredProductsText = tt[0].node.price.currencyCode + " " + tt[0].node.price.amount
        holder.binding.featuredProducts.text = featuredProductsText

        Glide.with(holder.itemView.context)
            .load(item.featuredImage.url)
            .into(holder.binding.img)

        holder.binding.wishList.setImageResource(
            if (selectedItems[position]) R.drawable.like else R.drawable.un_like
        )

        holder.binding.wishList.setOnClickListener {
            selectedItems[position] = false
            notifyItemChanged(position)
            onWishlistRemove(item, position)
        }

        holder.binding.root.setOnClickListener {
        }
    }

    override fun getItemCount() = items.size
}