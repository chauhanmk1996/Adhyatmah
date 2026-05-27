package com.app.adhyatmah.presentation.ui.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BaseRecyclerAdapter
import com.app.adhyatmah.databinding.ListItemTrendingSectionBinding
import com.app.adhyatmah.domain.model.TrendingSection
import com.bumptech.glide.Glide

class TrendingSectionAdapter(private var trendingSectionList: ArrayList<TrendingSection>,private val onSelected: (Int) -> Unit,
) : BaseRecyclerAdapter<ListItemTrendingSectionBinding>() {

    override fun getLayoutId(): Int = R.layout.list_item_trending_section

    override fun getItemCount(): Int {
        return trendingSectionList.size
    }

    override fun bind(binding: ListItemTrendingSectionBinding, position: Int) {
        binding.apply {
            val context = binding.root.context
            val trendingSection = trendingSectionList[position]

            tvName.text = trendingSection.title ?: ""
            trendingSection.image?.url?.let { url->
                Glide.with(context).load(url).into(binding.ivPic)
            }

            cvTrendingSection.setOnClickListener {
                onSelected(position)
            }
        }
    }
}