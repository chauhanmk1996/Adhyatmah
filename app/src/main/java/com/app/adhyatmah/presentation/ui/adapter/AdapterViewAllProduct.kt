package com.app.adhyatmah.presentation.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ListItemProductGridBinding
import com.app.adhyatmah.domain.model.view_all_product.response.Product

class AdapterViewAllProduct(
    var subList: List<Product>,
    private val showImage: Boolean,
    var onWishlistClick: (Int, Boolean) -> Unit,
    var onSubAdapterClick: (Int, Boolean, Product) -> Any,
) : RecyclerView.Adapter<AdapterViewAllProduct.ViewPager>() {
    class ViewPager(
        var binding: ListItemProductGridBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    private var selectedItems = subList.map { it.wishlist == true }.toMutableList()

    fun getItemAt(position: Int): Product {
        return subList[position]
    }

    fun updateData(newList: List<Product>) {
        subList = newList
        selectedItems = subList.map { it.wishlist == true }.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewPager {
        val binding =
            ListItemProductGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val layoutParams = binding.root.layoutParams

        if (layoutParams != null) {
            if (showImage) {
                // Grid size
                com.app.adhyatmah.utils.common_utils.CommonUtils
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height =
                    com.app.adhyatmah.utils.common_utils.CommonUtils.dpToPx(parent.context, 200)

            } else {
                // Horizontal list size
                layoutParams.width =
                    com.app.adhyatmah.utils.common_utils.CommonUtils.dpToPx(parent.context, 115)
                layoutParams.height =
                    com.app.adhyatmah.utils.common_utils.CommonUtils.dpToPx(parent.context, 140)
            }
            binding.root.layoutParams = layoutParams
        }

        return ViewPager(binding)
    }

    override fun getItemCount(): Int {
        return subList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewPager, position: Int) {
        val subListData = subList[position].featuredImage
        val product = subList[position]
        val price = subList[position]
        val isSelected = selectedItems[position]

        holder.binding.wishList.visibility = if (showImage) View.VISIBLE else View.VISIBLE

        Glide.with(holder.itemView.context)
            .load(subListData.url)
            .into(holder.binding.img)
        holder.binding.label.text = price.title
        holder.binding.featuredProducts.text =
            price.variants[0].price.currencyCode + " " + price.variants[0].price.amount


        holder.binding.wishList.setImageResource(
            if (isSelected) R.drawable.like else R.drawable.un_like
        )

        holder.binding.wishList.setOnClickListener {
            selectedItems[position] = !selectedItems[position]
            notifyItemChanged(position)
            onWishlistClick(position, selectedItems[position])
        }

        holder.binding.cardView.setOnClickListener {
            onSubAdapterClick(position, false, product)
        }
    }
}