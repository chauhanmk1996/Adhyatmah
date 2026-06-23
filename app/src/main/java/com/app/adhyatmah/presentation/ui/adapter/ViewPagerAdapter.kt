package com.app.adhyatmah.presentation.ui.adapter

import com.app.adhyatmah.R
import com.app.adhyatmah.base.BasePagerAdapter
import com.app.adhyatmah.databinding.ViewPagerHomeBannerBinding
import com.bumptech.glide.Glide
import com.app.adhyatmah.domain.model.home_banner_response.HomeBanner

class ViewPagerAdapter(
    private var viewPagerList: List<HomeBanner>,
    private val onSelected: (Int) -> Unit,
) :
    BasePagerAdapter<ViewPagerHomeBannerBinding>() {

    override fun getLayoutId(): Int = R.layout.view_pager_home_banner

    override fun getCount(): Int {
        return viewPagerList.size
    }

    override fun bind(binding: ViewPagerHomeBannerBinding, position: Int) {
        val url = viewPagerList[position].url
        val context = binding.root.context
        Glide.with(context).load(url).into(binding.ivBanner)

        binding.clBanner.setOnClickListener {
            onSelected(position)
        }
    }
}