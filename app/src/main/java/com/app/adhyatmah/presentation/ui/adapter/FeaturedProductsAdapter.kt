package com.app.adhyatmah.presentation.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adhyatmah.databinding.HomeFeaturedProductRecyclerBinding
import com.app.adhyatmah.domain.model.home_collection_Response.Payload

class FeaturedProductsAdapter(
    var context: Context,
    var data: Payload,
    var onViewAllClick1: Boolean,
    private val onViewAllClick: (Int) -> Unit,
    var onWishlistClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,

    //private val onWishlistClick: (Int, Boolean) -> Unit,
    var onSubAdapterClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit
   // private val onSubAdapterClick: (Int, Boolean) -> Unit
) :RecyclerView.Adapter<FeaturedProductsAdapter.ViewHolder>() {
    lateinit var adapter: FeaturedProductsSubAdapter
    inner  class ViewHolder(var binding:HomeFeaturedProductRecyclerBinding) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeaturedProductsAdapter.ViewHolder {
        var binding = HomeFeaturedProductRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeaturedProductsAdapter.ViewHolder, position: Int) {
        var list = data.collections[position]
        var subList = data.collections[position].products

        adapter = FeaturedProductsSubAdapter(
            context,
            subList,
            onViewAllClick1,
            onWishlistClick,
            onSubAdapterClick,
            position // <-- pass collection index here
        )


      //  adapter = FeaturedProductsSubAdapter(context,subList,onViewAllClick1,onWishlistClick,onSubAdapterClick)
        holder.binding.featuredProducts.text= list.title
        Log.d("TAG", "onBindVddiewHolder: ${list.description}")
        holder.binding.dis.text = if (list.description.isNullOrBlank()) {
            "We have your occasion covered"
        } else {
            list.description
        }

        /* holder.binding.dis.text = list.description ?:"We have your occasion covered"
 */
        holder.binding.subRecycler.adapter = adapter

        holder.binding.viewAll.setOnClickListener {

            onViewAllClick(position)

        }

    }

    fun isSubClick(position: Int,islike:Boolean){
    }

    override fun getItemCount(): Int {
        return data.collections.size
    }
}