package com.app.adhyatmah.presentation.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.ListItemHomeProductBinding
import com.app.adhyatmah.domain.model.home_collection_Response.Product
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.bumptech.glide.Glide

class HomeProductsAdapter(
    private val productList: ArrayList<Product>,
    private val showImage: Boolean,
    var onWishlistClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,
    var onSubAdapterClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,
    private val collectionIndex: Int,
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

            tvLabel.text = product.title ?: ""
            val price =
                "${product.variant?.price?.currencyCode ?: ""} ${product.variant?.price?.amount ?: ""}"
            tvPrice.text = price

            ivWishList.setOnClickListener {
                if (isSignUp == "1") {
                    product.wishlist = product.wishlist != true
                    notifyItemChanged(position)
                    onWishlistClick(collectionIndex, position, product.wishlist == true)
                } else {
                    showLoginPrompt(context)
                }
            }

            cvProduct.setOnClickListener {
                onSubAdapterClick(collectionIndex, position, false)
            }
        }
    }

    private fun showLoginPrompt(context: Context) {
        var dialog: AlertDialog? = null
        dialog = CommonUtils.showCustomAlertDialog(
            context,
            "Sign Up Required",
            "Please sign up to add items to your wishlist.",
            positiveButtonText = "Sign up",
            negativeButtonText = "Cancel",
            positiveButtonAction = {
                dialog?.dismiss()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                val bundle = Bundle()
                bundle.putString("previousScreen", "logout")
                bundle.putString("selectedImage", "0")
                intent.putExtras(bundle)
                context.startActivity(intent)
            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }
}