package com.app.adhyatmah.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.adhyatmah.databinding.ViewPagerBinding
import com.app.adhyatmah.domain.model.product_detail_response.Image

class ViewPagerProductDetailAdapter(
    var viewPagerList: List<Image>,
) : RecyclerView.Adapter<ViewPagerProductDetailAdapter.ViewModel>() {

    class ViewModel(var binding: ViewPagerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewModel {
        val binding = ViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val list = viewPagerList[position]

        Glide.with(holder.itemView.context)
            .load(list.url)
            .into(holder.binding.onboardingImage)
    }

    override fun getItemCount(): Int {
        return viewPagerList.size
    }
}