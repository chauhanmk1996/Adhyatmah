package com.app.adhyatmah.presentation.ui.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemHomeCollectionBinding
import com.app.adhyatmah.domain.model.home_collection_Response.HomeCollection

class HomeCollectionAdapter(
    private val collectionList: ArrayList<HomeCollection>,
    private val onViewAllClick1: Boolean,
    private val onViewAllClick: (Int) -> Unit,
    private val onWishlistClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,
    private val onSubAdapterClick: (collectionIndex: Int, productIndex: Int, isLiked: Boolean) -> Unit,
) : BaseRecyclerAdapter<ListItemHomeCollectionBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_home_collection

    override fun getItemCount(): Int {
        return collectionList.size
    }

    override fun bind(binding: ListItemHomeCollectionBinding, position: Int) {
        binding.apply {
            val collection = collectionList[position]

            tvCollectionName.text = collection.title ?: ""
            tvCollectionDesc.text = if (collection.description.isNullOrBlank()) {
                "We have your occasion covered"
            } else {
                collection.description
            }

            tvViewAll.setOnClickListener {
                onViewAllClick(position)
            }

            collection.products?.let { list ->
                val homeProductsAdapter = HomeProductsAdapter(
                    list,
                    onViewAllClick1,
                    onWishlistClick,
                    onSubAdapterClick,
                    position
                )
                rvProduct.adapter = homeProductsAdapter
            }
        }
    }
}