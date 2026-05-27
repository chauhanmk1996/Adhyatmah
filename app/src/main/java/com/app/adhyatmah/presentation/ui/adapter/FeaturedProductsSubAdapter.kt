package com.app.adhyatmah.presentation.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.databinding.HomeFeaturedProductSubRecyclerBinding
import com.app.adhyatmah.domain.model.home_collection_Response.Product
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.utils.common_utils.CommonUtils

class FeaturedProductsSubAdapter(
   var context: Context,
    var subList: List<Product>,
    private val showImage: Boolean,
   var onWishlistClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,

    //  var onWishlistClick: (Int, Boolean) -> Unit,
//    var onSubAdapterClick: (Int, Boolean) -> Unit
   var onSubAdapterClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,
   private val collectionIndex: Int  // <-- NEW

):RecyclerView.Adapter<FeaturedProductsSubAdapter.ViewPager>() {
    inner class ViewPager(var binding:HomeFeaturedProductSubRecyclerBinding):RecyclerView.ViewHolder(binding.root)
   // private val selectedItems = MutableList(itemCount) { false }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeaturedProductsSubAdapter.ViewPager {
        var binding = HomeFeaturedProductSubRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent,false)

        val layoutParams = binding.root.layoutParams

        if (layoutParams != null) {
            if (showImage) {
                // Grid size
                com.app.adhyatmah.utils.common_utils.CommonUtils
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = com.app.adhyatmah.utils.common_utils.CommonUtils.dpToPx(parent.context, 200)

            } else {
                // Horizontal list size
                layoutParams.width =com.app.adhyatmah.utils.common_utils.CommonUtils.dpToPx(parent.context, 115)
                layoutParams.height = com.app.adhyatmah.utils.common_utils.CommonUtils.dpToPx(parent.context, 140)
            }
            binding.root.layoutParams = layoutParams
        }



        return ViewPager(binding)
    }

    override fun getItemCount(): Int {
        return  subList.size  //subList.size
    }


    private fun showLoginPrompt() {
        var dialog: AlertDialog? = null
        dialog=  CommonUtils.showCustomAlertDialog(
            context ,
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
                context.startActivity(intent)            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }

    override fun onBindViewHolder(holder: FeaturedProductsSubAdapter.ViewPager, position: Int) {
         var subListData  =  subList[position].featuredImage
         var price  =  subList[position]


        Glide.with(holder.itemView.context).load(subListData.url).into(holder.binding.img)
        holder.binding.label.text = price.title
        holder.binding.featuredProducts.text = price.variant.price.currencyCode+" "+price.variant.price.amount

        var isSignUp = Preferences.getStringPreference(context, IS_LOGIN)
       /* if(isSignUp=="1"){

        }else{

        }*/

        holder.binding.wishList.setImageResource(
            if (price.wishlist) R.drawable.like
            else
                R.drawable.un_like
        )
        holder.binding.wishList.visibility = if (showImage) View.VISIBLE else View.VISIBLE

        /*holder.binding.wishList.setImageResource(
            if (isSelected) R.drawable.like else R.drawable.un_like
        )*/

        /*holder.binding.wishList.setOnClickListener {
            selectedItems[position] = !selectedItems[position]
            notifyItemChanged(position)
            // Callback
            onWishlistClick(position,selectedItems[position])
        }*/

        holder.binding.wishList.setOnClickListener {
            // Toggle wishlist state

            if (isSignUp == "1") {

                price.wishlist = !price.wishlist

                notifyItemChanged(position)
                onWishlistClick(collectionIndex,position, price.wishlist)

            }
            else{

                showLoginPrompt()
            }

        }

        holder.binding.cardView.setOnClickListener {
//            onSubAdapterClick(position,false)
            onSubAdapterClick(collectionIndex, position, false)

            Log.d("TAG", "onBindViddewHolder: $position")

        }


        }

    }