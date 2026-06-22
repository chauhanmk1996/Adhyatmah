package com.app.adhyatmah.presentation.ui.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemPopularPujasGridBinding
import com.app.adhyatmah.domain.model.PopularPooja
import com.app.adhyatmah.utils.formatViews
import com.app.adhyatmah.utils.getDigit
import com.bumptech.glide.Glide

class PopularPujasGridAdapter(
    private val popularPoojaList: ArrayList<PopularPooja>,
    private val onSelected: (PopularPooja) -> Unit,
) : BaseRecyclerAdapter<ListItemPopularPujasGridBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_popular_pujas_grid

    override fun getItemCount(): Int {
        return popularPoojaList.size
    }

    override fun bind(binding: ListItemPopularPujasGridBinding, position: Int) {
        binding.apply {
            val context = binding.root.context
            val popularPooja = popularPoojaList[position]

            popularPooja.image?.url?.let { url ->
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivPic)
            }

            tvViews.text = formatViews(popularPooja.views ?: 0)
            tvPoojaName.text = popularPooja.name ?: ""
            tvPoojaTime.text = popularPooja.duration ?: ""
            tvOfferPrice.text = (popularPooja.price?.getDigit() ?: "")
            tvOldPrice.text = (popularPooja.originalPrice?.getDigit() ?: "")

            clBookNow.setOnClickListener {
                onSelected(popularPooja)
            }
        }
    }
}