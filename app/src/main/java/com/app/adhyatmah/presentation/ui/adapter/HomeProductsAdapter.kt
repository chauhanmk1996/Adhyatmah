package com.app.adhyatmah.presentation.ui.adapter

import android.view.View
import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.ListItemHomeProductBinding
import com.app.adhyatmah.domain.model.home_collection_Response.Product
import com.bumptech.glide.Glide

class HomeProductsAdapter(
    private val productList: ArrayList<Product>,
    private val showImage: Boolean,
    private val collectionIndex: Int,
    private val onWishlistClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,
    private val onSubAdapterClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,
    private val singUpOpen: () -> Unit,
    var onAddToCartClick: (Product) -> Unit,
) : BaseRecyclerAdapter<ListItemHomeProductBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_home_product

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun bind(binding: ListItemHomeProductBinding, position: Int) {
        val context = binding.root.context
        binding.apply {
            val product = productList[position]

            product.featuredImage.url?.let { url ->
                Glide.with(context).load(url).into(ivPic)
            }

            val isSignUp = Preferences.getStringPreference(context, IS_LOGIN)
            ivWishList.visibility = if (showImage) View.VISIBLE else View.VISIBLE
            ivWishList.setImageResource(
                if (product.wishlist == true) R.drawable.like
                else
                    R.drawable.un_like
            )

            tvProductName.text = product.title ?: ""

            val offerPrice = "₹ ${product.variant?.price?.amount ?: ""}"
            tvOfferPrice.text = offerPrice

            val oldPrice = product.variant?.price?.originalPrice ?: ""
            tvOldPrice.text = oldPrice

            ivWishList.setOnClickListener {
                if (isSignUp == "1") {
                    product.wishlist = product.wishlist != true
                    notifyItemChanged(position)
                    onWishlistClick(collectionIndex, position, product.wishlist == true)
                } else {
                    singUpOpen()
                }
            }

            cvProduct.setOnClickListener {
                onSubAdapterClick(collectionIndex, position, false)
            }

            ivCart.setOnClickListener {
                onAddToCartClick(product)
            }
        }
    }
}